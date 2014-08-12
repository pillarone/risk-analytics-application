package org.pillarone.riskanalytics.application.ui.upload.finished.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.upload.finished.view.FinishedUploadsView
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component('uploadClearSelectedAction')
class ClearSelectedAction extends ResourceBasedAction {

    @Resource
    FinishedUploadsView finishedUploadsView

    ClearSelectedAction() {
        super('ClearSelectedSimulations')
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            finishedUploadsView.removeSelected()
        }
    }

    @Override
    boolean isEnabled() {
        finishedUploadsView.selectedSimulations.size() > 0
    }
}
