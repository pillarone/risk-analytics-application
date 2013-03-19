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

    BatchRunInfoService batchRunInfoService
    BatchDataTableModel batchDataTableModel
    Log LOG = LogFactory.getLog(PollingBatchRunAction)

    public PollingBatchRunAction(BatchDataTableModel batchDataTableModel) {
        this.batchDataTableModel = batchDataTableModel;
        batchRunInfoService = BatchRunInfoService.getService()
    }

    void actionPerformed(ActionEvent actionEvent) {
        synchronized (this) {
            try {
                batchDataTableModel.batchRunSimulationRuns.findAll {BatchRunSimulationRun batchRunSimulationRun ->
                    batchRunSimulationRun.simulationState != SimulationState.FINISHED && batchRunSimulationRun.simulationState != SimulationState.ERROR
                }.each {BatchRunSimulationRun batchRunSimulationRun ->
                    BatchRunSimulationRun run = batchRunInfoService.getBatchRunSimulationRun(batchRunSimulationRun)
                    if (run && run.simulationState != batchRunSimulationRun.simulationState) {
                        batchRunSimulationRun.simulationState = run.simulationState
                        batchDataTableModel.fireTableRowsUpdated(run)
                    }
                }
                batchDataTableModel.stopPollingTimer()
            } catch (Exception) {}
        }

    }


}
