package org.pillarone.riskanalytics.application.ui.upload.queue.view
import com.ulcjava.base.application.*
import groovy.util.logging.Log
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.upload.queue.model.UploadInfoPaneModel
import org.pillarone.riskanalytics.application.ui.util.I18NUtilities
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.upload.UploadState
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

import static com.ulcjava.base.shared.IDefaults.HORIZONTAL
import static org.pillarone.riskanalytics.core.upload.UploadState.*

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
@Log
class UploadInfoPane {

    private UploadState currentUIUploadState

    private ULCProgressBar progressBar
    private ULCLabel startTimeLabel
    private ULCLabel startTimeInfo
    private ULCLabel estimatedEndTimeLabel
    private ULCLabel estimatedEndTimeInfo
    private ULCLabel remainingTimeLabel
    private ULCLabel remainingTimeInfo
    private ULCBoxPane content

    @Resource
    GrailsApplication grailsApplication
    @Resource
    UploadInfoPaneModel uploadInfoPaneModel
    private final Map<UploadState, Closure> uiStates = [:]
    private MyUploadStateChangedListener listener = new MyUploadStateChangedListener()

    @PostConstruct
    private void initialize() {
        createUiStates()
        initComponents()
        layout()
        uploadInfoPaneModel.addUploadStateListener(listener)
    }

    @PreDestroy
    void unregister() {
        uploadInfoPaneModel.removeUploadStateListener(listener)
    }

    private void createUiStates() {
        uiStates[PENDING] = {
            startTimeInfo.text = "-"
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = "-"
            progressBar.value = 0
            progressBar.indeterminate = false
            progressBar.string = getText("SimulationNotRunningMessage")
        }

        uiStates[UPLOADING] = {
            startTimeInfo.text = uploadInfoPaneModel.uploadStartTime
            remainingTimeInfo.text = uploadInfoPaneModel.remainingTime
            estimatedEndTimeInfo.text = uploadInfoPaneModel.estimatedEndTime
            progressBar.value = uploadInfoPaneModel.progress
            progressBar.indeterminate = false
            progressBar.string = UIUtils.getText(UploadInfoPane, "SimulationComplete", ["${uploadInfoPaneModel.progress}"])
        }

        uiStates[DONE] = {
            startTimeInfo.text = uploadInfoPaneModel.uploadStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = uploadInfoPaneModel.uploadEndTime
            progressBar.indeterminate = false
            progressBar.value = 100
            progressBar.string = getText("Done")
        }

        uiStates[CANCELED] = {
            startTimeInfo.text = uploadInfoPaneModel.uploadStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = "-"
            progressBar.indeterminate = false
            progressBar.value = uploadInfoPaneModel.progress
            progressBar.string = getText("Canceled")
        }

        uiStates[ERROR] = {
            startTimeInfo.text = uploadInfoPaneModel.uploadStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = uploadInfoPaneModel.uploadEndTime
            progressBar.indeterminate = false
            progressBar.string = getText("Error")
        }
    }

    private String getText(String key) {
        return UIUtils.getText(UploadInfoPane, key)
    }


    private void initComponents() {
        progressBar = new ULCProgressBar(HORIZONTAL, 0, 100)
        progressBar.stringPainted = true
        progressBar.name = "progress"

        startTimeLabel = new ULCLabel(getText("StartTime") + ":")
        startTimeInfo = new ULCLabel()
        startTimeInfo.name = "startTime"

        estimatedEndTimeLabel = new ULCLabel(getText("EstimatedEndTime") + ":")
        estimatedEndTimeInfo = new ULCLabel()
        estimatedEndTimeInfo.name = "endTime"

        remainingTimeLabel = new ULCLabel(getText("RemainingTime") + ":")
        remainingTimeInfo = new ULCLabel()
        remainingTimeInfo.name = "remainingTime"
    }

    private void layout() {
        content = new ULCBoxPane(4, 0)
        content.add(4, ULCBoxPane.BOX_EXPAND_CENTER, progressBar)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, startTimeLabel)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, startTimeInfo)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, estimatedEndTimeLabel)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, estimatedEndTimeInfo)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, remainingTimeLabel)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, remainingTimeInfo)
        content.add(ULCFiller.createHorizontalGlue())
    }

    void showAlert() {
        new ULCAlert(UlcUtilities.getWindowAncestor(content), "Error occurred during upload", I18NUtilities.getExceptionText(uploadInfoPaneModel.errorMessage), "Ok").show()
    }

    ULCBoxPane getContent() {
        return content
    }

    private class MyUploadStateChangedListener implements IUploadStateListener {

        @Override
        void uploadStateChanged(UploadState uploadState) {
            if (uploadState != currentUIUploadState) {
                log.info "Updating UI to ${uploadState.toString()}"
            }
            uiStates[uploadState].call()
            currentUIUploadState = uploadState
            if (currentUIUploadState == ERROR && shouldShowAlert()) {
                showAlert()
            }
        }

        private boolean shouldShowAlert() {
            return currentUser?.username == uploadInfoPaneModel?.uploadOwner?.username
        }

        private Person getCurrentUser() {
            UserManagement.currentUser
        }
    }
}