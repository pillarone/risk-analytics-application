package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel
import org.pillarone.riskanalytics.core.batch.BatchRunInfoService
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class PollingBatchRunAction implements IActionListener {
    private static final Log LOG = LogFactory.getLog(PollingBatchRunAction)

    BatchRunInfoService batchRunInfoService
    BatchDataTableModel batchDataTableModel

    public PollingBatchRunAction(BatchDataTableModel batchDataTableModel) {
        this.batchDataTableModel = batchDataTableModel;
        batchRunInfoService = BatchRunInfoService.service
    }

    void actionPerformed(ActionEvent actionEvent) {
        try {
            batchDataTableModel.batch.simulations.findAll { Simulation simulation ->
                simulation.simulationState != SimulationState.FINISHED && simulation.simulationState != SimulationState.ERROR
            }.each { Simulation simulation ->
                SimulationState newState = batchRunInfoService.getSimulationState(simulation)
                if (newState && newState != simulation.simulationState) {
                    simulation.simulationState = newState
                    batchDataTableModel.fireTableRowsUpdated(simulation)
                }
            }
            batchDataTableModel.stopPollingTimer()
        } catch (Exception e) {
            LOG.error('failed to update batch runs', e)
        }
    }
}
