package org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.action

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.batch.view.action.AbstractFindResultsInTreeAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.FinishedSimulationView
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FindResultsInTreeAction extends AbstractFindResultsInTreeAction {

    @Resource
    FinishedSimulationView finishedSimulationView

    @Override
    protected List<Simulation> getSimulations() {
        finishedSimulationView.selectedSimulations.simulation
    }

    @Override
    boolean isEnabled() {
        finishedSimulationView.selectedSimulations.size() > 0
    }
}
