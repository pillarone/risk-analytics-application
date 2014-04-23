package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch

class RunBatchAction extends SelectionTreeAction {

    RunBatchAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("RunBatch", tree, model)
    }

    void doActionPerformed(ActionEvent event) {
        Batch batchToRun = selectedItem as Batch
        if (batchToRun != null) {
            if (batchToRun.executed) {
                new I18NAlert("BatchAlreadyExecuted").show()
            } else {
                batchRunService.runBatch(batchToRun)
            }
        }
    }

    BatchRunService getBatchRunService() {
        Holders.grailsApplication.mainContext.getBean('batchRunService', BatchRunService)
    }
}
