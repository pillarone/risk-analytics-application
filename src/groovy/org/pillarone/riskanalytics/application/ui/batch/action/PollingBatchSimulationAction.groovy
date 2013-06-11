package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.core.batch.BatchRunInfoService
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class PollingBatchSimulationAction implements IActionListener {

    BatchRunInfoService batchRunInfoService

    List<ISimulationListener> simulationListeners

    public PollingBatchSimulationAction() {
        batchRunInfoService = BatchRunInfoService.getService()
        simulationListeners = []
    }

    void actionPerformed(ActionEvent actionEvent) {
        try {
            batchRunInfoService.getFinished(System.currentTimeMillis() - 2000).each {Simulation simulation ->
                simulationListeners.each {ISimulationListener simulationListener ->
                    simulationListener.simulationEnd simulation, null
                }
            }
        } catch (Exception ex) {
            //ignore exception
        }
    }

    public void addSimulationListener(ISimulationListener simulationListener) {
        simulationListeners << simulationListener
    }
}


