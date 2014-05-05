package org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.FinishedSimulationView
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class ClearSelectedAction extends ResourceBasedAction {

    @Resource
    FinishedSimulationView finishedSimulationView

    ClearSelectedAction() {
        super('ClearSelectedSimulations')
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            finishedSimulationView.removeSelected()
        }
    }

    @Override
    boolean isEnabled() {
        finishedSimulationView.selectedSimulations.size() > 0
    }
}
