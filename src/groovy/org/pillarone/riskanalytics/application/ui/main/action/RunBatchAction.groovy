package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.action.SingleItemAction
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch

class RunBatchAction extends SingleItemAction {

    RunBatchAction(ULCTableTree tree) {
        super("RunBatch", tree)
    }

    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            batchRunService.runBatch(selectedItem as Batch)
        }
    }

    private BatchRunService getBatchRunService() {
        Holders.grailsApplication.mainContext.getBean('batchRunService', BatchRunService)
    }

    @Override
    boolean isEnabled() {
        if (!super.isEnabled()) {
            return false
        }
        selectedItem instanceof Batch && selectedItem.isValidToRun()
    }
}
