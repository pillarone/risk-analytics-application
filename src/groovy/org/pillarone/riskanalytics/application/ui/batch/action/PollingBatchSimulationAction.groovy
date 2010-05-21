package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.core.batch.BatchRunInfoService
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class PollingBatchSimulationAction implements IActionListener {

    BatchRunInfoService batchRunInfoService

    List<ISimulationListener> simulationListeners

    public PollingBatchSimulationAction() {
        batchRunInfoService = BatchRunInfoService.getService()
        simulationListeners = []
    }

    void actionPerformed(ActionEvent actionEvent) {
        batchRunInfoService?.executedSimulations?.each {Simulation simulation ->
            simulationListeners.each {ISimulationListener simulationListener ->
                simulationListener.simulationEnd simulation, null
            }
        }
        batchRunInfoService?.executedSimulations?.clear()
    }

    public void addSimulationListener(ISimulationListener simulationListener) {
        simulationListeners << simulationListener
    }
}


