package org.pillarone.riskanalytics.application.ui.upload.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.upload.view.UploadBatchView
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class OpenResultsAction extends ResourceBasedAction {

    private final UploadBatchView uploadBatchView

    OpenResultsAction(UploadBatchView uploadBatchView) {
        super('OpenResults')
        this.uploadBatchView = uploadBatchView
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            Simulation simulation = uploadBatchView.selectedSimulationRowInfos.first().simulation
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(UIItemFactory.createItem(simulation)))
        }
    }

    @Override
    boolean isEnabled() {
        uploadBatchView.selectedSimulationRowInfos.size() == 1
    }
}
