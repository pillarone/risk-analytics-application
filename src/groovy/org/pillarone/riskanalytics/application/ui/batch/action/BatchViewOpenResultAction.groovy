package org.pillarone.riskanalytics.application.ui.batch.action
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.batch.model.BatchRowInfo
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory

class BatchViewOpenResultAction extends ResourceBasedAction {
    private final BatchView batchView

    BatchViewOpenResultAction(BatchView batchView) {
        super('BatchOpenresultAction')
        this.batchView = batchView
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(UIItemFactory.createItem(batchView.selectedBatchRowInfos.first().template)))
        }
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
