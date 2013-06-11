package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.batch.BatchRunner

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
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
