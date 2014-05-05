package org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.finished.FinishedSimulationsViewModel
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class ClearAllAction extends ResourceBasedAction {

    @Resource
    FinishedSimulationsViewModel finishedSimulationsViewModel

    ClearAllAction() {
        super('ClearAllSimulations')
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            finishedSimulationsViewModel.clearAll()
        }
    }

    @Override
    boolean isEnabled() {
        finishedSimulationsViewModel.finishedSimulationsTableModel.rowCount > 0
    }
}
