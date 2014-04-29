package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.action.SingleItemAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch

class RunBatchAction extends SingleItemAction {

    RunBatchAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("RunBatch", tree, model)
    }

    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            batchRunService.runBatch(selectedItem as Batch)
        }
    }

    BatchRunService getBatchRunService() {
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
