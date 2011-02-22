package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCFrame
import models.core.CoreModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.output.NoOutput
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.testframework.operator.*
import static org.pillarone.riskanalytics.core.simulation.SimulationState.*

class SimulationActionsPaneTests extends AbstractSimpleFunctionalTest {

    private SimulationActionsPaneModel model
    private SimulationActionsPane pane

    protected void doStart() {
        LocaleResources.setTestMode()

        FileImportService.importModelsIfNeeded(["Core"])

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"


        Simulation simulation = new Simulation("Simulation")
        simulation.modelClass = CoreModel
        simulation.parameterization = new Parameterization("CoreParameters")
        simulation.template = new ResultConfiguration("CoreResultConfiguration")
        model = new TestActionPaneModel([getSimulation: { simulation }, getOutputStrategy: { new NoOutput() }] as ISimulationProvider)

        pane = new SimulationActionsPane(model)
        pane.simulationPropertyChanged(true)
        model.simulation = simulation
        frame.setContentPane(pane.content)
        frame.visible = true
    }


    public void stop() {
        LocaleResources.clearTestMode()
    }

    //TODO: re-enable when all features are supported by core
    /*public void testRunSimulation() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCButtonOperator run = new ULCButtonOperator(frame, new ComponentByNameChooser("${SimulationActionsPane.getSimpleName()}.run"))
        ULCButtonOperator stop = new ULCButtonOperator(frame, new ComponentByNameChooser("stop"))
        ULCButtonOperator cancel = new ULCButtonOperator(frame, new ComponentByNameChooser("cancel"))
        ULCButtonOperator openResults = new ULCButtonOperator(frame, new ComponentByNameChooser("${SimulationActionsPane.getSimpleName()}.openResults"))
        ULCProgressBarOperator progress = new ULCProgressBarOperator(frame, new ComponentByNameChooser("progress"))
        ULCLabelOperator startTime = new ULCLabelOperator(frame, new ComponentByNameChooser("startTime"))
        ULCLabelOperator endTime = new ULCLabelOperator(frame, new ComponentByNameChooser("endTime"))
        ULCLabelOperator remainingTime = new ULCLabelOperator(frame, new ComponentByNameChooser("remainingTime"))

        run.clickMouse()

        waitForStatus(INITIALIZING)
        assertFalse run.enabled
        assertFalse stop.enabled
        assertTrue cancel.enabled
        assertFalse openResults.enabled

        model.runner++
        waitForStatus(RUNNING)

        assertFalse run.enabled
        assertTrue stop.enabled
        assertTrue cancel.enabled
        assertFalse openResults.enabled

        model.runner++
        model.runner++
        model.runner++
        waitForStatus(SAVING_RESULTS)

        assertFalse run.enabled
        assertFalse stop.enabled
        assertFalse cancel.enabled
        assertFalse openResults.enabled

        model.runner++
        waitForStatus(POST_SIMULATION_CALCULATIONS)

        assertFalse run.enabled
        assertFalse stop.enabled
        assertTrue cancel.enabled
        assertFalse openResults.enabled

        model.runner++
        waitForStatus(FINISHED)

        assertTrue run.enabled
        assertFalse stop.enabled
        assertFalse cancel.enabled
        assertTrue openResults.enabled
    }

    public void testStopSimulation() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCButtonOperator run = new ULCButtonOperator(frame, new ComponentByNameChooser("${SimulationActionsPane.getSimpleName()}.run"))
        ULCButtonOperator stop = new ULCButtonOperator(frame, new ComponentByNameChooser("stop"))
        ULCButtonOperator cancel = new ULCButtonOperator(frame, new ComponentByNameChooser("cancel"))
        ULCButtonOperator openResults = new ULCButtonOperator(frame, new ComponentByNameChooser("${SimulationActionsPane.getSimpleName()}.openResults"))
        ULCProgressBarOperator progress = new ULCProgressBarOperator(frame, new ComponentByNameChooser("progress"))
        ULCLabelOperator startTime = new ULCLabelOperator(frame, new ComponentByNameChooser("startTime"))
        ULCLabelOperator endTime = new ULCLabelOperator(frame, new ComponentByNameChooser("endTime"))
        ULCLabelOperator remainingTime = new ULCLabelOperator(frame, new ComponentByNameChooser("remainingTime"))

        run.clickMouse()

        waitForStatus(INITIALIZING)
        assertFalse run.enabled
        assertFalse stop.enabled
        assertTrue cancel.enabled
        assertFalse openResults.enabled

        model.runner++
        waitForStatus(RUNNING)

        assertFalse run.enabled
        assertTrue stop.enabled
        assertTrue cancel.enabled
        assertFalse openResults.enabled

        stop.clickMouse()
        waitForStatus(STOPPED)

        assertTrue run.enabled
        assertFalse stop.enabled
        assertFalse cancel.enabled
        assertTrue openResults.enabled
    }

    public void testCancelSimulation() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        assertNotNull frame

        ULCButtonOperator run = new ULCButtonOperator(frame, new ComponentByNameChooser("${SimulationActionsPane.getSimpleName()}.run"))
        ULCButtonOperator stop = new ULCButtonOperator(frame, new ComponentByNameChooser("stop"))
        ULCButtonOperator cancel = new ULCButtonOperator(frame, new ComponentByNameChooser("cancel"))
        ULCButtonOperator openResults = new ULCButtonOperator(frame, new ComponentByNameChooser("${SimulationActionsPane.getSimpleName()}.openResults"))
        ULCProgressBarOperator progress = new ULCProgressBarOperator(frame, new ComponentByNameChooser("progress"))
        ULCLabelOperator startTime = new ULCLabelOperator(frame, new ComponentByNameChooser("startTime"))
        ULCLabelOperator endTime = new ULCLabelOperator(frame, new ComponentByNameChooser("endTime"))
        ULCLabelOperator remainingTime = new ULCLabelOperator(frame, new ComponentByNameChooser("remainingTime"))

        run.clickMouse()

        waitForStatus(INITIALIZING)
        assertFalse run.enabled
        assertFalse stop.enabled
        assertTrue cancel.enabled
        assertFalse openResults.enabled

        model.runner++
        waitForStatus(RUNNING)

        assertFalse run.enabled
        assertTrue stop.enabled
        assertTrue cancel.enabled
        assertFalse openResults.enabled

        cancel.clickMouse()
        waitForStatus(CANCELED)

        assertTrue run.enabled
        assertFalse stop.enabled
        assertFalse cancel.enabled
        assertFalse openResults.enabled
    }*/

    private void waitForStatus(SimulationState simulationState) {
        int delay = 0
        while (pane.currentUISimulationState != simulationState && delay < 5000) {
            Thread.sleep 250
        }
        if (delay >= 5000) {
            throw new IllegalStateException("Expected status ${simulationState.toString()} not reached.")
        }
    }
}
