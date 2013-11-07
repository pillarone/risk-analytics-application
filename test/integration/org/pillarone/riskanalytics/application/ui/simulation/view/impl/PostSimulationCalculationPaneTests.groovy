package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ULCFrameOperator
import org.junit.Assert
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

        runVoidCommand(new AssertParametersCommand(pane, 12, true))

        ULCListOperator list = new ULCListOperator(frame, new ComponentByNameChooser("lossPercentile-list"))
        list.selectItem(10)

        ULCButtonOperator remove = new ULCButtonOperator(frame, new ComponentByNameChooser("lossPercentile-remove"))
        remove.clickMouse()

        runVoidCommand(new AssertParametersCommand(pane, 11, false))
    }
}

class OnServerAssertion extends ServerSideCommand {
    private PostSimulationCalculationPane pane
    private int expectedPercentileLossCount
    private boolean containsPercentile

    OnServerAssertion(PostSimulationCalculationPane pane, int expectedPercentileLossCount, boolean containsPercentile) {
        this.pane = pane
        this.expectedPercentileLossCount = expectedPercentileLossCount
        this.containsPercentile = containsPercentile
    }

    protected void proceedOnServer() {
        Assert.assertFalse(pane.model.standardDeviation)
        Assert.assertEquals(expectedPercentileLossCount, pane.model.percentileLoss.size())
        if (containsPercentile) {
            Assert.assertTrue(pane.model.percentileLoss.contains(95d))
        } else {
            Assert.assertFalse(pane.model.percentileLoss.contains(95d))
        }
    }

}
