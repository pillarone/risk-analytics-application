package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.core.output.batch.BatchRunner

public class RunBatchSimulationAction extends BatchSimulationSelectionAction {

    public RunBatchSimulationAction() {
        super("RunBatchSimulation");
    }

    public void doActionPerformed(ActionEvent event) {
        BatchRunSimulationRun batchRunSimulationRun = BatchRunner.getService().getSimulationRun(model.batchRun, getSelectedSimulationRun())
        BatchRunner.getService().runSimulation(batchRunSimulationRun)
    }


}






