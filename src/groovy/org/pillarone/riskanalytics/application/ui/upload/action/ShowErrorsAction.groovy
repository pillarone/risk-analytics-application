package org.pillarone.riskanalytics.application.ui.upload.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.upload.model.SimulationRowInfo
import org.pillarone.riskanalytics.application.ui.upload.view.UploadBatchView
import org.pillarone.riskanalytics.application.ui.util.IResourceBundleResolver
import org.pillarone.riskanalytics.core.upload.UploadValidationError

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
            List<String> errorMessages = info.errors.collect { UploadValidationError error -> createMessage(error) }
            String title = resourceBundleResolver.getText(ShowErrorsAction, 'validationErrors')
            ULCAlert alert = new ULCAlert(UlcUtilities.getRootPane(uploadBatchView.content), title, errorMessages.join('\n'), 'OK')
            alert.messageType = ULCAlert.ERROR_MESSAGE
            alert.show()
        }
    }

    @Override
    boolean isEnabled() {
        List<SimulationRowInfo> infos = uploadBatchView.selectedSimulationRowInfos
        infos.size() == 1 ? !infos.first().valid : false
    }


    private IResourceBundleResolver getResourceBundleResolver() {
        Holders.grailsApplication.mainContext.getBean('resourceBundleResolver', IResourceBundleResolver)
    }

    private String createMessage(UploadValidationError error) {
        switch (error.reason) {
            case UploadValidationError.REASON.NO_PROFILE:
                return resourceBundleResolver.getText(ShowErrorsAction, 'profile')
            case UploadValidationError.REASON.WRONG_RANDOM_SEED:
                return resourceBundleResolver.getText(ShowErrorsAction, 'randomSeed', [error.simulationParameterValue, error.profileParameterValue])
            case UploadValidationError.REASON.WRONG_NUMBER_OF_ITERATION:
                return resourceBundleResolver.getText(ShowErrorsAction, 'numberOfIterations', [error.simulationParameterValue, error.profileParameterValue])
            case UploadValidationError.REASON.WRONG_TEMPLATE:
                return resourceBundleResolver.getText(ShowErrorsAction, 'template', [error.simulationParameterValue, error.profileParameterValue])
            case UploadValidationError.REASON.WRONG_PARAMETER_COUNT:
                return resourceBundleResolver.getText(ShowErrorsAction, 'parameterCount', [error.simulationParameterValue, error.profileParameterValue])
            case UploadValidationError.REASON.WRONG_PARAMETER:
                return resourceBundleResolver.getText(ShowErrorsAction, 'parameter', [error.path, error.simulationParameterValue, error.profileParameterValue])
        }
    }
}
