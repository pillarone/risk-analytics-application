package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class DeleteBatchSimulationAction extends BatchSimulationSelectionAction {

    public DeleteBatchSimulationAction() {
        super("DeleteBatchSimulation");
    }

    public void doActionPerformed(ActionEvent event) {
        Simulation run = selectedSimulationRun
        int rowIndex = model.getRowIndex(run)
        if (rowIndex != -1) {
            BatchRunService.service.deleteSimulationRun(model.batch, run)
            model.fireRowDeleted(rowIndex)
        }
    }

}
