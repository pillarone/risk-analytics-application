package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.output.batch.OutputStrategyFactory
import org.joda.time.DateTime

class AddToBatchAction extends RunSimulationAction {

    public AddToBatchAction(SimulationActionsPaneModel model) {
        super("AddToBatch", model);
    }

    void doActionPerformed(ActionEvent event) {
        boolean isValid = validate(event)
        if (isValid) {
            addToBatch()
        }
    }

    private void addToBatch() {
        try {
            BatchRun batchRun = model.batchRunComboBoxModel.selectedObject
            if (!batchRun) {
                String newBatchRunName = model.batchRunComboBoxModel.getSelectedItem()
                if (!newBatchRunName || "" == newBatchRunName)
                    newBatchRunName = "${new DateTime()}"
                batchRun = new BatchRun(name: newBatchRunName, executionTime: new DateTime())
                BatchRun.withTransaction {
                    batchRun.save()
                }
                trace("Batch run created: $newBatchRunName")
                model.mainModel.fireBatchAdded(batchRun)
                model.batchRunComboBoxModel.addItem(batchRun)
            }
            trace("Adding simulation ${model.simulation.name} to batch with name $batchRun.name")
            BatchRunSimulationRun batchRunSimulationRun = BatchRunService.service.createBatchRunSimulationRun(batchRun, model.simulation, OutputStrategyFactory.getEnum(model.outputStrategy.class))
            def count = BatchRunSimulationRun.countByBatchRun(batchRun).toString()
            String message = UIUtils.getText(this.class, "succes", [model.simulation.name, count])
            model.notifySimulationToBatchAdded(message, batchRunSimulationRun)
        } catch (Exception ex) {
            model.notifySimulationToBatchAdded(UIUtils.getText(this.class, "error"), null)
        }
    }


}
