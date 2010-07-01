package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.engine.RunSimulationService
import org.pillarone.riskanalytics.core.simulation.engine.SimulationConfiguration
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRunner
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import java.text.SimpleDateFormat
import java.text.DateFormat
import groovy.time.TimeCategory
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.RunSimulationAction
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.StopSimulationAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.CancelSimulationAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.ISimulationProvider
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.OpenResultsAction

/**
 * The view model for the SimulationActionsPane.
 * It controls the simulation provided by the ISimulationProvider (run, stop, cancel)
 * and provides information about the current simulation state. 
 */
class SimulationActionsPaneModel {

    protected SimulationRunner runner
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm")
    private List<ISimulationListener> listeners = []

    Simulation simulation
    ICollectorOutputStrategy outputStrategy

    RunSimulationAction runSimulationAction
    StopSimulationAction stopSimulationAction
    CancelSimulationAction cancelSimulationAction
    OpenResultsAction openResultsAction

    ISimulationProvider simulationProvider
    P1RATModel mainModel

    public SimulationActionsPaneModel(ISimulationProvider provider, P1RATModel mainModel) {
        this.mainModel = mainModel
        simulationProvider = provider
        runSimulationAction = new RunSimulationAction(this)
        stopSimulationAction = new StopSimulationAction(this)
        cancelSimulationAction = new CancelSimulationAction(this)
        openResultsAction = new OpenResultsAction(this)
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
}
