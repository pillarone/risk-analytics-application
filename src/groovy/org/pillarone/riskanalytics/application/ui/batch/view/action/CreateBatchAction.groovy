package org.pillarone.riskanalytics.application.ui.batch.view.action

import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.view.NodeNameDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

import static com.ulcjava.base.application.UlcUtilities.getWindowAncestor

class CreateBatchAction extends ResourceBasedAction {

    private final BatchView batchView

    CreateBatchAction(BatchView batchView) {
        super("CreateBatch")
        this.batchView = batchView
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        Batch batch = batchRunService.createBatch(parameterizations)
        BatchUIItem batchUIItem = new BatchUIItem(batch)
        NodeNameDialog nameDialog = new NodeNameDialog(getWindowAncestor(batchView.content), batchUIItem)
        nameDialog.okAction = { String name ->
            batch.name = name
            riskAnalyticsMainModel.openItem(null, batchUIItem)
            batch.changed = true
        }
        nameDialog.show()
    }

    private BatchRunService getBatchRunService() {
        Holders.grailsApplication.mainContext.getBean('batchRunService', BatchRunService)
    }

    private RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    private List<Parameterization> getParameterizations() {
        batchView.selectedBatchRowInfos.parameterization
    }
}
