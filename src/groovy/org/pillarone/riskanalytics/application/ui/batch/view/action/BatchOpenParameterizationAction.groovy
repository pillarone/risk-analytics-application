package org.pillarone.riskanalytics.application.ui.batch.view.action

import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.batch.model.BatchRowInfo
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.core.model.Model

class BatchOpenParameterizationAction extends ResourceBasedAction {
    private final BatchView batchView

    BatchOpenParameterizationAction(BatchView batchView) {
        super('BatchOpenParameterizationAction')
        this.batchView = batchView
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        BatchRowInfo info = batchView.selectedBatchRowInfos.first()
        Model model = info.modelClass.newInstance() as Model
        riskAnalyticsMainModel.openItem(model, UIItemFactory.createItem(info.parameterization, model))
    }

    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    @Override
    boolean isEnabled() {
        batchView.selectedBatchRowInfos.size() == 1
    }
}
