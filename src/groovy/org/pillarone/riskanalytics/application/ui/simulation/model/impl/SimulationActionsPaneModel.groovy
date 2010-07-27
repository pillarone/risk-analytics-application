package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import groovy.time.TimeCategory
import java.text.DateFormat
import java.text.SimpleDateFormat
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.result.view.ItemsComboBoxModel
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.ISimulationProvider
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationActionsPane
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.engine.RunSimulationService
import org.pillarone.riskanalytics.core.simulation.engine.SimulationConfiguration
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRunner
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.*

/**
 * The view model for the SimulationActionsPane.
 * It controls the simulation provided by the ISimulationProvider (run, stop, cancel)
 * and provides information about the current simulation state.
 */
class SimulationActionsPaneModel {

    protected SimulationRunner runner
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm")
    private List<ISimulationListener> listeners = []

    volatile Simulation simulation
    ICollectorOutputStrategy outputStrategy

    RunSimulationAction runSimulationAction
    StopSimulationAction stopSimulationAction
    CancelSimulationAction cancelSimulationAction
    OpenResultsAction openResultsAction

    ItemsComboBoxModel<BatchRun> batchRunComboBoxModel
    AddToBatchAction addToBatchAction

    ISimulationProvider simulationProvider
    P1RATModel mainModel

    String batchMessage

    public SimulationActionsPaneModel(ISimulationProvider provider, P1RATModel mainModel) {
        this.mainModel = mainModel
        simulationProvider = provider
        runSimulationAction = new RunSimulationAction(this)
        stopSimulationAction = new StopSimulationAction(this)
        cancelSimulationAction = new CancelSimulationAction(this)
        openResultsAction = new OpenResultsAction(this)

        batchRunComboBoxModel = new ItemsComboBoxModel<BatchRun>(BatchRun.list())
        addToBatchAction = new AddToBatchAction(this)
    }

    String getText(String key) {
        return UIUtils.getText(SimulationActionsPaneModel, key)
    }

    void runSimulation() {
        simulation.save()
        runner = SimulationRunner.createRunner()
        RunSimulationService.getService().runSimulation(runner, new SimulationConfiguration(simulation: simulation, outputStrategy: outputStrategy))
        notifySimulationStart()
    }

    void stopSimulation() {
        runner.stop()
    }

    void cancelSimulation() {
        runner.cancel()
    }

    int getProgress() {
        runner.getProgress()
    }

    int getIterationsDone() {
        runner.currentScope.iterationsDone
    }

    SimulationState getSimulationState() {
        runner.getSimulationState()
    }

    String getEstimatedEndTime() {
        Date estimatedSimulationEnd = runner.getEstimatedSimulationEnd()
        if (estimatedSimulationEnd != null) {
            return dateFormat.format(estimatedSimulationEnd)
        }
        return "-"
    }

    String getSimulationStartTime() {
        Date estimatedSimulationEnd = simulation.start
        if (estimatedSimulationEnd != null) {
            return dateFormat.format(estimatedSimulationEnd)
        }
        return "-"
    }

    String getSimulationEndTime() {
        Date estimatedSimulationEnd = simulation.end
        if (estimatedSimulationEnd != null) {
            return dateFormat.format(estimatedSimulationEnd)
        }
        return "-"
    }

    String getRemainingTime() {
        String result = "-"
        Date end = runner.getEstimatedSimulationEnd()
        if (end != null) {
            use(TimeCategory) {
                def duration = end - new Date()
                result = "$duration.hours h $duration.minutes m $duration.seconds s"
            }
        }
        return result
    }

    void addSimulationListener(ISimulationListener listener) {
        listeners.add(listener)
    }

    void removeSimulationListener(ISimulationListener listener) {
        listeners.remove(listener)
    }

    void notifySimulationStart() {
        listeners*.simulationStart(simulation)
    }

    void notifySimulationStop() {
        listeners*.simulationEnd(simulation, runner.currentScope.model)
    }

    void notifySimulationToBatchAdded(String message) {
        batchMessage = message
        ISimulationListener pane = listeners.find {it.class.name == SimulationActionsPane.class.name}
        pane?.simulationToBatchAdded()
        mainModel.fireRowAdded()
    }

    String getErrorMessage() {
        Exception simulationException = runner.error?.error

        String exceptionMessage = simulationException.message
        if (exceptionMessage == null) {
            exceptionMessage = simulationException.class.name
        }
        List words = exceptionMessage.split(" ") as List
        StringBuffer text = new StringBuffer()
        int lineLength = 0
        for (String s in words) {
            if (lineLength + s.length() > 70) {
                text << "\n"
                lineLength = 0
            }
            text << s + " "
            lineLength += (s.length() + 1)
        }

        return text.toString()
    }

}