package org.pillarone.riskanalytics.application.ui.parameterization.view

import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import com.ulcjava.base.application.ULCFrame
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNodeFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.TestMultiDimensionalParameterModel
import org.pillarone.riskanalytics.core.parameterization.PeriodMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker
import models.core.CoreModel
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCPopupMenuOperator
import com.ulcjava.testframework.operator.AWTComponentOperator
import com.ulcjava.testframework.operator.ULCCheckBoxMenuItemOperator
import com.ulcjava.testframework.operator.ULCTableOperator
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import com.ulcjava.testframework.ServerSideCommand
import com.ulcjava.base.application.util.Point
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.ULCScrollPane

class PeriodMultiDimensionalParameterViewTests extends AbstractSimpleFunctionalTest {

    Parameterization parameterization
    CoreModel coreModel
    ULCFrame frame2


    @Override
    protected void doStart() {
        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"
        frame.setSize(1500, 600)

        coreModel = new CoreModel()
        coreModel.init()
        coreModel.injectComponentNames()

        parameterization = new Parameterization("Name", CoreModel)
        def mdp = new PeriodMatrixMultiDimensionalParameter([[1.0, 0.0, 0.0], [0.0, 1.0, 0.0], [0d, 0d, 1d]], [['exampleOutputComponent', 'exampleOutputComponent', 'exampleOutputComponent'], [1, 2, 3]], ITestComponentMarker)
        ParameterHolder holder = ParameterHolderFactory.getHolder("path", 0, mdp)
        parameterization.addParameter(holder)

        def node = ParameterizationNodeFactory.getNode(holder.path, parameterization, coreModel)
        MultiDimensionalParameterModel multiDimensionalParameterModel = new TestMultiDimensionalParameterModel(null, node, 1)
        PeriodMultiDimensionalParameterView view = new PeriodMultiDimensionalParameterView(multiDimensionalParameterModel)
        frame.contentPane = view.getContent()
        frame.visible = true
    }



    void testUpdateAndSave() {
        ULCFrameOperator frameOp = new ULCFrameOperator("test")
        ULCButtonOperator applyOp = new ULCButtonOperator(frameOp, new ComponentByNameChooser("applyButton"))
        ULCTextFieldOperator periodOp = new ULCTextFieldOperator(frameOp, new ComponentByNameChooser("periodTextField"))
        ULCButtonOperator compSelectOp = new ULCButtonOperator(frameOp, new ComponentByNameChooser("selectComponents"))
        ULCTableOperator originalTable = new ULCTableOperator(frameOp, new ComponentByNameChooser("multiDimTable"))
        //Select all components..
        compSelectOp.clickForPopup()
        ULCPopupMenuOperator popOp = new ULCPopupMenuOperator(frameOp, new ComponentByNameChooser("componentPopupMenu"))
        popOp.getComponents().each {AWTComponentOperator op ->
            assertTrue(op instanceof ULCCheckBoxMenuItemOperator)
            ULCCheckBoxMenuItemOperator checkBoxMenuItemOperator = (ULCCheckBoxMenuItemOperator) op
            if (!checkBoxMenuItemOperator.isSelected()) {
                checkBoxMenuItemOperator.clickMouse()
            }
        }
        //Set periods to 5
        periodOp.getFocus()
        periodOp.enterText("4")
        //Apply new vals & save
        applyOp.clickMouse()
        //enter values into table..
        for (int row = 2; row < originalTable.getRowCount(); row++) {
            for (int col = 3; col < originalTable.getColumnCount(); col++) {
                Object value = originalTable.getValueAt(row, col)
                if (row != col - 1) {
                    AWTComponentOperator op = originalTable.clickForEdit(row, col)
                    if (op instanceof ULCTextFieldOperator) {
                        ULCTextFieldOperator textFieldOperator = (ULCTextFieldOperator) op
                        Double toEnter = (row + "." + col).toDouble()
                        op.enterText(toEnter.toString())
                    } else {
                        println(op.getClass())
                    }
                }
            }
        }

        parameterization.save()

        //Create a new frame & table with the loaded param, needs to happen serverside..
        runVoidCommand(new ServerSideCommand() {
            @Override
            protected void proceedOnServer() {
                Parameterization loaded = new Parameterization("Name", CoreModel)
                loaded.load()
                MultiDimensionalParameterHolder param = loaded.getParameters("path")[0]
                PeriodMatrixMultiDimensionalParameter loadedParam = param.businessObject
                ParameterHolder holder = loaded.getParameterHolder("path", 0)
                def node = ParameterizationNodeFactory.getNode(holder.path, loaded, coreModel)
                MultiDimensionalParameterModel loadedModel = new TestMultiDimensionalParameterModel(null, node, 1)
                PeriodMultiDimensionalParameterView view = new PeriodMultiDimensionalParameterView(loadedModel)
                frame2 = new ULCFrame("test2")
                frame2.name = "test2"
                frame2.setDefaultCloseOperation(ULCFrame.TERMINATE_ON_CLOSE)
                frame2.setSize(1500, 600)
                frame2.setLocation(500, 500)
                frame2.contentPane = view.content
                frame2.visible = true
            }
        });
        //Wait a while...

        //At this point we should have the original table & new one launched with the param loaded from db
        ULCFrameOperator frame2Op = new ULCFrameOperator("test2")

        ULCTableOperator loadedTable = new ULCTableOperator(frame2Op, new ComponentByNameChooser("multiDimTable"))
        //Compare tables...
        assertEquals(originalTable.getRowCount(), loadedTable.getRowCount())
        assertEquals(originalTable.getColumnCount(), loadedTable.getColumnCount())
        for (int i = 0; i < loadedTable.getRowCount(); i++) {
            for (int j = 0; j < loadedTable.getColumnCount(); j++) {
                //need to scroll to cell due to lazy loading..
                loadedTable.scrollToCell(i, j)
                originalTable.scrollToCell(i, j)
                Object newVal = loadedTable.getValueAt(i, j)
                Object oldVal = originalTable.getValueAt(i, j)
                //ensure values are equal
                assertEquals(oldVal, newVal)
            }
        }
        ULCTextFieldOperator period2Op = new ULCTextFieldOperator(frame2Op, new ComponentByNameChooser("periodTextField"))
        assertEquals(period2Op.getText(), periodOp.getText())
    }
}