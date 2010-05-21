package org.pillarone.riskanalytics.application.ui.batch.action

import org.pillarone.riskanalytics.core.output.batch.BatchRunner

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.core.output.SimulationRun

public class RunBatchSimulationAction extends BatchSimulationSelectionAction {

    public RunBatchSimulationAction() {
        super("RunBatchSimulation");
    }

    public void doActionPerformed(ActionEvent event) {
        BatchRunSimulationRun batchRunSimulationRun = BatchRunner.getService().getSimulationRun(model.batchRun, getSelectedSimulationRun())
        BatchRunner.getService().runSimulation(batchRunSimulationRun)
    }


}

public class EditBatchSimulationAction extends BatchSimulationSelectionAction {

    public EditBatchSimulationAction() {
        super("EditBatchSimulation");
    }

    public void doActionPerformed(ActionEvent event) {
        Object source = event.source
        SimulationRun run = getSelectedSimulationRun()
        model.openItem(run)
    }
}

public class DeleteBatchSimulationAction extends BatchSimulationSelectionAction {

    public DeleteBatchSimulationAction() {
        super("DeleteBatchSimulation");
    }

    public void doActionPerformed(ActionEvent event) {
        SimulationRun run = getSelectedSimulationRun()
        int rowIndex = model.getRowIndex(run)
        if (rowIndex != -1) {
            BatchRunner.getService().deleteSimulationRun(model.batchRun, run)
            model.fireRowDeleted(rowIndex)
        }
    }

}

public class ChangeBatchSimulationPriorityAction extends BatchSimulationSelectionAction {

    int step

    public ChangeBatchSimulationPriorityAction(BatchDataTableModel model, int step) {
        super("ChangeBatchSimulationPriorityAction" + (step > 0 ? "ToBottom" : "ToTop"));
        super.@model = model
        this.@step = step;

    }

    public ChangeBatchSimulationPriorityAction(int step) {
        super("ChangeBatchSimulationPriorityAction" + (step > 0 ? "ToTop" : "ToBottom"));
        this.step = step
    }

    public void doActionPerformed(ActionEvent event) {
        SimulationRun run = getSelectedSimulationRun()
        int rowIndex = model.getRowIndex(run)
        if (rowIndex != -1) {
            model.batchRun.batchRunService.changePriority(model.batchRun, run, step)
            model.firePriorityChanged rowIndex, step
        }
    }

}




abstract class BatchSimulationSelectionAction extends ResourceBasedAction {
    protected BatchDataTableModel model

    public BatchSimulationSelectionAction(String actionName) {
        super(actionName);
    }

    SimulationRun getSelectedSimulationRun() {
        SimulationRun run = model.selectedRun
        return run
    }

}
