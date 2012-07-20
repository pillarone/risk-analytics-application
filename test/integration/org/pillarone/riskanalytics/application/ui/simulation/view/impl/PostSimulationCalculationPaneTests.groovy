package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ULCFrameOperator
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.testframework.operator.ULCCheckBoxOperator
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.ServerSideCommand
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCListOperator

class PostSimulationCalculationPaneTests extends AbstractSimpleFunctionalTest {

    PostSimulationCalculationPane pane


    protected void doStart() {
        LocaleResources.setTestMode()
        ModellingItemFactory.clear()

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        pane = new PostSimulationCalculationPane(new PostSimulationCalculationPaneModel())
        frame.setContentPane(pane.content)
        frame.visible = true
    }


    public void stop() {
        LocaleResources.clearTestMode()
    }

    public void testView() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCCheckBoxOperator standardDeviation = new ULCCheckBoxOperator(frame, new ComponentByNameChooser("standardDeviation"))
        assertTrue(standardDeviation.selected)
        standardDeviation.changeSelection(false)

        ULCTextFieldOperator valueField = new ULCTextFieldOperator(frame, new ComponentByNameChooser("lossPercentile-value"))
        valueField.enterText("95")

        ULCButtonOperator add = new ULCButtonOperator(frame, new ComponentByNameChooser("lossPercentile-add"))
        add.clickMouse()

        runVoidCommand(new ServerSideCommand() {
            @Override
            protected void proceedOnServer() {
                assertFalse(pane.model.standardDeviation)

                assertEquals(12, pane.model.percentileLoss.size())
                assertTrue(pane.model.percentileLoss.contains(95d))
            }

        })

        ULCListOperator list = new ULCListOperator(frame, new ComponentByNameChooser("lossPercentile-list"))
        list.selectItem(10)

        ULCButtonOperator remove = new ULCButtonOperator(frame, new ComponentByNameChooser("lossPercentile-remove"))
        remove.clickMouse()

        runVoidCommand(new ServerSideCommand() {
            @Override
            protected void proceedOnServer() {
                assertEquals(11, pane.model.percentileLoss.size())
                assertFalse(pane.model.percentileLoss.contains(95d))
            }

        })

    }
}
