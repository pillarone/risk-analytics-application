package org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.FinishedSimulationView
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

import static org.pillarone.riskanalytics.core.simulation.SimulationState.FINISHED

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class OpenResultsAction extends ResourceBasedAction {

    @Resource
    FinishedSimulationView finishedSimulationView

    @Resource
    RiskAnalyticsMainModel riskAnalyticsMainModel

    OpenResultsAction() {
        super("OpenResults");
    }

    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            Simulation simulation = finishedSimulationView.selectedSimulations.first().simulation
            riskAnalyticsMainModel.notifyOpenDetailView(UIItemFactory.createItem(simulation))
        }
    }

    @Override
    boolean isEnabled() {
        List<SimulationRuntimeInfo> simulations = finishedSimulationView.selectedSimulations
        simulations.size() == 1 && simulations.first().simulationState == FINISHED
    }
}
