package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.action

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.batch.action.AbstractSelectItemsInTreeAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SimulationQueueView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class SimulationQueueViewFindParameterizationsInTreeAction extends AbstractSelectItemsInTreeAction<Parameterization> {

    static final String ACTION_NAME = 'FindParameterizationsInTree'

    @Resource
    SimulationQueueView simulationQueueView

    SimulationQueueViewFindParameterizationsInTreeAction() {
        super(ACTION_NAME)
    }

    @Override
    protected List<Parameterization> getItems() {
        simulationQueueView.selectedSimulations.simulation.parameterization
    }

    @Override
    boolean isEnabled() {
        simulationQueueView.selectedSimulations.size() > 0
    }
}
