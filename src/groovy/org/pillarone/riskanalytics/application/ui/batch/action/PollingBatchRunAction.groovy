package org.pillarone.riskanalytics.application.ui.batch.action
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel
import org.pillarone.riskanalytics.core.batch.BatchRunInfoService
import org.pillarone.riskanalytics.core.output.SimulationRun
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
            batchDataTableModel.batchRun.simulationRuns.findAll { SimulationRun simulationRun ->
                simulationRun.simulationState != SimulationState.FINISHED && simulationRun.simulationState != SimulationState.ERROR
            }.each { SimulationRun simulationRun->
                SimulationState newState = batchRunInfoService.getSimulationState(simulationRun)
                if (newState && newState != simulationRun.simulationState) {
                    simulationRun.simulationState = newState
                    batchDataTableModel.fireTableRowsUpdated(simulationRun)
                }
            }
            batchDataTableModel.stopPollingTimer()
        } catch (Exception e) {
            LOG.error('failed to update batch runs', e)
        }
    }
}
