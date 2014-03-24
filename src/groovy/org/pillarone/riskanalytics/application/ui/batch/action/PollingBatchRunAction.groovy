package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.core.batch.BatchRunInfoService
import org.pillarone.riskanalytics.core.simulation.SimulationState

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
            batchDataTableModel.batchRunSimulationRuns.findAll { BatchRunSimulationRun batchRunSimulationRun ->
                batchRunSimulationRun.simulationState != SimulationState.FINISHED && batchRunSimulationRun.simulationState != SimulationState.ERROR
            }.each { BatchRunSimulationRun batchRunSimulationRun ->
                SimulationState newState = batchRunInfoService.getSimulationState(batchRunSimulationRun)
                if (newState && newState != batchRunSimulationRun.simulationState) {
                    batchRunSimulationRun.simulationState = newState
                    batchDataTableModel.fireTableRowsUpdated(batchRunSimulationRun)
                }
            }
            batchDataTableModel.stopPollingTimer()
        } catch (Exception e) {
            LOG.error('failed to update batch runs', e)
        }
    }
}
