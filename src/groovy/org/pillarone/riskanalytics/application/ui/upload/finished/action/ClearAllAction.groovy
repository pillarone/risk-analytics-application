package org.pillarone.riskanalytics.application.ui.upload.finished.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.upload.finished.model.FinishedUploadsViewModel
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component('uploadClearAllAction')
class ClearAllAction extends ResourceBasedAction {

    @Resource
    FinishedUploadsViewModel finishedUploadsViewModel

    ClearAllAction() {
        super('ClearAllSimulations')
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            finishedUploadsViewModel.clearAll()
        }
    }

    @Override
    boolean isEnabled() {
        finishedUploadsViewModel.finishedUploadsTableModel.rowCount > 0
    }
}
