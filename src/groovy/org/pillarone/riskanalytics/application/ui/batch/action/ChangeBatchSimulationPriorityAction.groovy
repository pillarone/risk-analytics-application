package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel
import org.pillarone.riskanalytics.core.output.SimulationRun

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
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
