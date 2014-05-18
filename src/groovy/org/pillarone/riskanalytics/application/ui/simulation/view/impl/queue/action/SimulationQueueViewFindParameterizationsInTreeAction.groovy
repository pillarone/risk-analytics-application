package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.action

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.batch.view.action.AbstractFindParameterizationsInTreeAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SimulationQueueView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class SimulationQueueViewFindParameterizationsInTreeAction extends AbstractFindParameterizationsInTreeAction {
    @Resource
    SimulationQueueView simulationQueueView

    @Override
    protected List<Parameterization> getParameterizations() {
        simulationQueueView.selectedSimulations.simulation.parameterization
    }

    @Override
    boolean isEnabled() {
        simulationQueueView.selectedSimulations.size() > 0
    }
}
