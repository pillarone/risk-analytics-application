package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.core.output.batch.OutputStrategyFactory
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.application.ui.util.UIUtils


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
                batchRun = new BatchRun(name: newBatchRunName, executionTime: new Date())
                BatchRun.withTransaction {
                    batchRun.save()
                }
                model.mainModel.addItem(batchRun)
                model.batchRunComboBoxModel.addItem(batchRun)
            }
            BatchRunService.getService().addSimulationRun(batchRun, model.simulation, OutputStrategyFactory.getEnum(model.outputStrategy.class))
            model.setBatchMessage(UIUtils.getText(this.class, "succes"))
        } catch (Exception ex) {
            model.setBatchMessage(UIUtils.getText(this.class, "error"))
        }
    }


}
