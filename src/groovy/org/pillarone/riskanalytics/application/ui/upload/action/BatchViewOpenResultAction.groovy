package org.pillarone.riskanalytics.application.ui.upload.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.upload.model.SimulationRowInfo
import org.pillarone.riskanalytics.application.ui.upload.view.UploadBatchView

class BatchViewOpenResultAction extends ResourceBasedAction {
    private final UploadBatchView uploadBatchView

    BatchViewOpenResultAction(UploadBatchView uploadBatchView) {
        super('OpenresultAction')
        this.uploadBatchView = uploadBatchView
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(UIItemFactory.createItem(uploadBatchView.selectedSimulationRowInfos.first().template)))
        }
    }

    @Override
    boolean isEnabled() {
        List<SimulationRowInfo> infos = uploadBatchView.selectedSimulationRowInfos
        if (infos.size() == 1) {
            return infos.first().template
        }
        return false
    }
}
