package org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.action

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.batch.action.AbstractSelectItemsInTreeAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.FinishedSimulationView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedSimulationsFindParameterizationsInTreeAction extends AbstractSelectItemsInTreeAction<Parameterization> {
    static final String ACTION_NAME = 'FindParameterizationsInTree'

    @Resource
    FinishedSimulationView finishedSimulationView

    FinishedSimulationsFindParameterizationsInTreeAction() {
        super(ACTION_NAME)
    }

    @Override
    protected List<Parameterization> getItems() {
        finishedSimulationView.selectedSimulations.simulation.parameterization
    }

    @Override
    boolean isEnabled() {
        finishedSimulationView.selectedSimulations.size() > 0
    }
}
