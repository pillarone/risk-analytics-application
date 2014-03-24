package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.core.batch.BatchRunService

public class RunBatchSimulationAction extends BatchSimulationSelectionAction {

    public RunBatchSimulationAction() {
        super("RunBatchSimulation");
    }

    public void doActionPerformed(ActionEvent event) {
        BatchRunService.service.runBatchRunSimulation(model.batchRun, selectedSimulationRun)
    }
}






