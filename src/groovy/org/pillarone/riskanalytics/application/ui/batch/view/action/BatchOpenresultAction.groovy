package org.pillarone.riskanalytics.application.ui.batch.view.action

import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.batch.model.BatchRowInfo
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

class BatchOpenresultAction extends ResourceBasedAction {
    private final BatchView batchView

    BatchOpenresultAction(BatchView batchView) {
        super('BatchOpenresultAction')
        this.batchView = batchView
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        ResultConfiguration template = batchView.selectedBatchRowInfos.first().template
        if (template) {
            template.load()
            riskAnalyticsMainModel.notifyOpenDetailView((Model) template.modelClass.newInstance(), template)
        }
    }

    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    @Override
    boolean isEnabled() {
        List<BatchRowInfo> infos = batchView.selectedBatchRowInfos
        if (infos.size() == 1) {
            return infos.first().template
        }
        return false
    }
}
