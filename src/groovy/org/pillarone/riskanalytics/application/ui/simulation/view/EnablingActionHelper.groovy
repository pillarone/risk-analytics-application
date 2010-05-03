package org.pillarone.riskanalytics.application.ui.simulation.view

import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel
import org.pillarone.riskanalytics.core.simulation.SimulationState

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class EnablingActionHelper {

    AbstractConfigurationModel model

    private def runningSimulationStates = [SimulationState.INITIALIZING, SimulationState.RUNNING, SimulationState.SAVING_RESULTS, SimulationState.SAVING_RESULTS, SimulationState.POST_SIMULATION_CALCULATIONS]
    private def notRunningSimulationStates = [SimulationState.NOT_RUNNING, SimulationState.FINISHED, SimulationState.STOPPED, SimulationState.CANCELED, SimulationState.ERROR]



    public EnablingActionHelper(model) {
        this.model = model;
    }

    public boolean isOpenResultButtonEnabled() {
        return isEnabled(notRunningSimulationStates) && model.isOpenResultEnabled()
    }

    public boolean isStopButtonEnabled() {
        return isEnabled(runningSimulationStates) && !model.stopAction.clicked
    }

    public boolean isCancelButtonEnabled() {
        return isEnabled(runningSimulationStates)
    }

    public boolean isRunButtonEnabled() {
        return isEnabled(notRunningSimulationStates) && model.isSimulationStartEnabled()
    }

    public boolean isAddToBatchButtonEnabled() {
        return isEnabled(notRunningSimulationStates)
    }


    private boolean isEnabled(List<SimulationState> simulationStates) {
        boolean state = false
        for (SimulationState simulationState: simulationStates) {
            state = model.getSimulationRunner().simulationState == simulationState
            if (state)
                return state
        }
        return state;
    }

}
