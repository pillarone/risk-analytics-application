package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.event.ActionEvent
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class AddToBatchAction extends RunSimulationAction {

    private final RiskAnalyticsMainModel mainModel

    public AddToBatchAction(SimulationActionsPaneModel model, RiskAnalyticsMainModel mainModel) {
        super("AddToBatch", model);
        this.mainModel = mainModel
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
                String newBatchRunName = model.batchRunComboBoxModel.selectedItem
                if (!newBatchRunName || "" == newBatchRunName)
                    newBatchRunName = "${new DateTime()}"
                batchRun = new BatchRun(name: newBatchRunName)
                BatchRun.withTransaction {
                    batchRun.save()
                }
                trace("Batch run created: $newBatchRunName")
                mainModel.fireBatchAdded(batchRun)
                model.batchRunComboBoxModel.addItem(batchRun)
            }
            trace("Adding simulation ${model.simulation.name} to batch with name $batchRun.name")
            Simulation simulation = model.simulation
            int count = BatchRunService.service.createBatchRunSimulationRun(batchRun, simulation)
            String message = UIUtils.getText(this.class, 'success', [model.simulation.name, count.toString()])
            model.batchMessage = message
            mainModel.fireRowAdded(simulation.simulationRun)
        } catch (Exception ignored) {
            model.batchMessage = UIUtils.getText(this.class, 'error')
        }
    }
}
