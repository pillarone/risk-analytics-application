package org.pillarone.riskanalytics.application.ui.upload.action

import com.ulcjava.base.application.event.ActionEvent
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.upload.view.UploadBatchView

@CompileStatic
class DeleteSimulationsAction extends ResourceBasedAction {
    private final UploadBatchView uploadBatchView

    DeleteSimulationsAction(UploadBatchView uploadBatchView) {
        super('DeleteParameterizations')
        this.uploadBatchView = uploadBatchView
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            uploadBatchView.removeSelectedSimulations()
        }
    }

    @Override
    boolean isEnabled() {
        uploadBatchView.selectedSimulationRowInfos.size() > 0
    }
}
