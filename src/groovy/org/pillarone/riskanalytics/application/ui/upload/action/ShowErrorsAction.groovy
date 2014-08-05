package org.pillarone.riskanalytics.application.ui.upload.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.upload.model.SimulationRowInfo
import org.pillarone.riskanalytics.application.ui.upload.view.UploadBatchView

class ShowErrorsAction extends ResourceBasedAction {

    private final UploadBatchView uploadBatchView

    ShowErrorsAction(UploadBatchView uploadBatchView) {
        super('ShowValidationErrorsAction')
        this.uploadBatchView = uploadBatchView
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            SimulationRowInfo info = uploadBatchView.selectedSimulationRowInfos.first()
            ULCAlert alert = new ULCAlert(UlcUtilities.getRootPane(uploadBatchView.content), 'Validation Errors', info.errors.error.join(' '), 'OK')
            alert.messageType = ULCAlert.ERROR_MESSAGE
            alert.show()
        }
    }

    @Override
    boolean isEnabled() {
        List<SimulationRowInfo> infos = uploadBatchView.selectedSimulationRowInfos
        infos.size() == 1 ? !infos.first().valid : false
    }
}
