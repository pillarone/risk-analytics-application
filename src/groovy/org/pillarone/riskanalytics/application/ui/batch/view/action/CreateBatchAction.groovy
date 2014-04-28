package org.pillarone.riskanalytics.application.ui.batch.view.action

import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch

class CreateBatchAction extends ResourceBasedAction {
    private final BatchView batchView

    CreateBatchAction(BatchView batchView) {
        super("CreateBatch")
        this.batchView = batchView
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        batchView.selectedBatchRowInfos.parameterization
        Batch batch = batchRunService.createBatch(batchView.selectedBatchRowInfos.parameterization)
        riskAnalyticsMainModel.openItem(null, new BatchUIItem(batch))
    }

    private BatchRunService getBatchRunService() {
        Holders.grailsApplication.mainContext.getBean('batchRunService', BatchRunService)
    }

    private RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }
}
