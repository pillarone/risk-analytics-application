package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.core.batch.BatchRunInfoService

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class PollingBatchRunAction implements IActionListener {

    BatchRunInfoService batchRunInfoService
    BatchDataTableModel batchDataTableModel

    public PollingBatchRunAction(BatchDataTableModel batchDataTableModel) {
        this.batchDataTableModel = batchDataTableModel;
        batchRunInfoService = BatchRunInfoService.getService()
    }

    void actionPerformed(ActionEvent actionEvent) {
        synchronized (this) {
            List<BatchRun> removedBatchRunSimulationRuns = []
            batchRunInfoService?.executedBatchRunSimulationRuns?.each {BatchRunSimulationRun batchRunSimulationRun ->
                if (batchDataTableModel?.batchRun?.name == batchRunSimulationRun.batchRun.name) {
                    batchDataTableModel.fireTableRowsUpdated(batchRunSimulationRun)
                    removedBatchRunSimulationRuns << batchRunSimulationRun
                }
            }
            if (!removedBatchRunSimulationRuns.isEmpty())
                batchRunInfoService?.executedBatchRunSimulationRuns?.removeAll(removedBatchRunSimulationRuns)
        }

    }


}
