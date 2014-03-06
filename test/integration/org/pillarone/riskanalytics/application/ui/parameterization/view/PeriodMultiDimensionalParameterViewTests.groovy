package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.ServerSideCommand
import com.ulcjava.testframework.operator.*
import models.core.CoreModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNodeFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.TestMultiDimensionalParameterModel
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker
import org.pillarone.riskanalytics.core.parameterization.PeriodMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory

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
        frame.contentPane = view.content
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
        popOp.components.each { AWTComponentOperator op ->
            assertTrue(op instanceof ULCCheckBoxMenuItemOperator)
            ULCCheckBoxMenuItemOperator checkBoxMenuItemOperator = (ULCCheckBoxMenuItemOperator) op
            if (!checkBoxMenuItemOperator.selected) {
                checkBoxMenuItemOperator.clickMouse()
            }
        }
        //Set periods to 5
        periodOp.getFocus()
        periodOp.enterText("4")
        //Apply new vals & save
        applyOp.clickMouse()
        //enter values into table..
        for (int row = 2; row < originalTable.rowCount; row++) {
            for (int col = 3; col < originalTable.columnCount; col++) {
                if (row != col - 1) {
                    AWTComponentOperator op = originalTable.clickForEdit(row, col)
                    if (op instanceof ULCTextFieldOperator) {
                        ULCTextFieldOperator textFieldOperator = (ULCTextFieldOperator) op
                        Double toEnter = (row + "." + col).toDouble()
                        op.clearText()
                        //TODO  due to https://www.canoo.com/jira/browse/UBA-8756
                        //there will be a lot of IllegalArgumentExceptions if the next line is commented in.
                        //After ulc upgrade to at least 7.2.1 it will work again.
                        //Also this test is not really a test. We should check, if the values in the parametrization are correct!
//                        op.enterText(toEnter.toString())
                    }
                }
            }
        }

        parameterization.save()

        //Create a new frame & table with the loaded param, needs to happen serverside..
        runVoidCommand(new MakeFrameVisibleCommand(coreModel, frame2));
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

class MakeFrameVisibleCommand extends ServerSideCommand {
    CoreModel model
    ULCFrame frame

    MakeFrameVisibleCommand(CoreModel model, ULCFrame frame) {
        this.model = model
        this.frame = frame
    }

    @Override
    protected void proceedOnServer() {
        Parameterization loaded = new Parameterization("Name", CoreModel)
        loaded.load()
        MultiDimensionalParameterHolder param = loaded.getParameters("path")[0]
        PeriodMatrixMultiDimensionalParameter loadedParam = param.businessObject
        ParameterHolder holder = loaded.getParameterHolder("path", 0)
        def node = ParameterizationNodeFactory.getNode(holder.path, loaded, model)
        MultiDimensionalParameterModel loadedModel = new TestMultiDimensionalParameterModel(null, node, 1)
        PeriodMultiDimensionalParameterView view = new PeriodMultiDimensionalParameterView(loadedModel)
        frame = new ULCFrame("test2")
        frame.name = "test2"
        frame.setDefaultCloseOperation(ULCFrame.TERMINATE_ON_CLOSE)
        frame.setSize(1500, 600)
        frame.setLocation(500, 500)
        frame.contentPane = view.content
        frame.visible = true
    }

}