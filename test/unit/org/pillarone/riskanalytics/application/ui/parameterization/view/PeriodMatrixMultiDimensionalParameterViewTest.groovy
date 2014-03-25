package org.pillarone.riskanalytics.application.ui.parameterization.view

import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import com.ulcjava.base.application.ULCComponent
import models.core.CoreModel
import org.pillarone.riskanalytics.core.parameterization.PeriodMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNodeFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import com.ulcjava.testframework.operator.ULCPopupMenuOperator
import com.ulcjava.testframework.operator.AWTComponentOperator
import com.ulcjava.testframework.operator.ULCCheckBoxMenuItemOperator
import org.pillarone.riskanalytics.core.components.Component
import com.ulcjava.testframework.operator.ULCTableOperator
import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import org.pillarone.riskanalytics.core.components.ComponentUtils

class PeriodMatrixMultiDimensionalParameterViewTest extends AbstractP1RATTestCase {
    PeriodMatrixMultiDimensionalParameter multiDimensionalParameter
    MultiDimensionalParameterModel multiDimensionalParameterModel

    @Override
    ULCComponent createContentPane() {
        ULCClipboard.install()
        CoreModel coreModel = new CoreModel()
        coreModel.init()
        coreModel.injectComponentNames()

        def mdp = new PeriodMatrixMultiDimensionalParameter([[1, 0, 0], [0, 1, 0], [0, 0, 1]], [['exampleOutputComponent', 'exampleOutputComponent', 'exampleOutputComponent'], [1, 2, 3]], ITestComponentMarker)
        ParameterHolder holder = ParameterHolderFactory.getHolder("path", 0, mdp)

        Parameterization parameterization = new Parameterization("Name", CoreModel)
        parameterization.addParameter(holder)

        def node = ParameterizationNodeFactory.getNode(holder.path, parameterization, coreModel)
        multiDimensionalParameterModel = new MyDummyModel(null, node, 1)
        multiDimensionalParameter = multiDimensionalParameterModel.multiDimensionalParameterInstance
        PeriodMultiDimensionalParameterView view = new PeriodMultiDimensionalParameterView(multiDimensionalParameterModel)
        return view.getContent()
    }

    void testSetNumberOfPeriods() {
        selectAllComponents(true)
        //Set number of periods and ensure parameter updates correctly
        setPeriodNumber(5)
        apply()
        assert multiDimensionalParameter.getMaxPeriod() == 5

        setPeriodNumber(10)
        apply()
        assert multiDimensionalParameter.getMaxPeriod() == 10

        setPeriodNumber(1)
        apply()
        assertTrue(multiDimensionalParameter.getMaxPeriod() == 1)
    }

    void testComponentSelection() {
        selectAllComponents(false)
        apply()

        ULCTableOperator multiDimTableOp = new ULCTableOperator(getMainFrameOperator(), new ComponentByNameChooser("multiDimTable"))
        assertEquals(multiDimTableOp.getRowCount(), multiDimensionalParameter.getTitleRowCount())
        //table has got 1 extra column for indices..
        assertEquals(multiDimTableOp.getColumnCount(), multiDimensionalParameter.getTitleColumnCount() + 1)

        //Now select all components..
       selectAllComponents(true)
        setPeriodNumber(1)
        apply()
        //Get all available components from the param's sim model
        List possibleComps = []
        multiDimensionalParameter.getSimulationModel().getMarkedComponents(multiDimensionalParameter.getMarkerClass()).each {Component comp ->
            possibleComps << comp.getName()
        }
        List inTable = []
        (multiDimensionalParameter.getTitleColumnCount()..multiDimTableOp.getRowCount() - 1).each {rowIndex ->
            Object val = multiDimTableOp.getValueAt(rowIndex, 1)
            inTable.add((String) multiDimTableOp.getValueAt(rowIndex, 1))
        }
        //Ensure we've selected all...
        assertTrue(inTable.size() == possibleComps.size())
        possibleComps.each {String comp ->
            assertTrue(inTable.contains(ComponentUtils.getNormalizedName(comp)))
        }
    }

    void testDiagonal() {
        selectAllComponents(true)
        setPeriodNumber(10)
        //Apply to update param
        apply()
        //Check row & column count
        assertTrue(multiDimensionalParameter.getColumnCount() == multiDimensionalParameter.getTitleColumnCount() + (2 * 10))
        assertTrue(multiDimensionalParameter.getRowCount() == multiDimensionalParameter.getTitleRowCount() + (2 * 10))
        //Check diagonal
        for (int i = multiDimensionalParameter.getTitleColumnCount(); i < multiDimensionalParameter.getColumnCount(); i++) {
            assertTrue(multiDimensionalParameter.getValueAt(i, i) == 1)
        }
    }

    private void selectAllComponents(boolean select) {
        ULCButtonOperator compSelectOp = new ULCButtonOperator(getMainFrameOperator(), new ComponentByNameChooser("selectComponents"))
        //Select components and ensure that param updates correctly..
        compSelectOp.clickForPopup()
        ULCPopupMenuOperator popOp = new ULCPopupMenuOperator(getMainFrameOperator(), new ComponentByNameChooser("componentPopupMenu"))
        //Deselect all comps, ensure table is empty..
        popOp.getComponents().each {AWTComponentOperator op ->
            assertTrue(op instanceof ULCCheckBoxMenuItemOperator)
            ULCCheckBoxMenuItemOperator checkBoxMenuItemOperator = (ULCCheckBoxMenuItemOperator) op
            if (select) {
                if (!checkBoxMenuItemOperator.isSelected()) {
                    checkBoxMenuItemOperator.clickMouse()
                }
            } else {
                if (checkBoxMenuItemOperator.isSelected()) {
                    checkBoxMenuItemOperator.clickMouse()
                }

            }
        }
    }

    private void setPeriodNumber(int periods) {
        ULCTextFieldOperator periodOp = new ULCTextFieldOperator(getMainFrameOperator(), new ComponentByNameChooser("periodTextField"))
        periodOp.getFocus()
        periodOp.enterText(Integer.toString(periods))
    }

    private void apply() {
        new ULCButtonOperator(getMainFrameOperator(), new ComponentByNameChooser("applyButton")).clickMouse()
    }

//    void testView() {
//        sleep 5000000
//    }

    class MyDummyModel extends MultiDimensionalParameterModel {

        public MyDummyModel(model, node, columnIndex) {
            super(model, node, columnIndex);
        }

        public void save() {
        }

        public String getPathAsString() {
            ""
        }

        public void modelChanged() {

        }

        public def getMultiDimensionalParameterInstance() {
            return multiDimensionalParameter
        }
    }
}
