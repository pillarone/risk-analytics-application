package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SimulationInfoPane
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class OpenCurrentResultsAction extends ResourceBasedAction {

    @Resource
    SimulationInfoPane simulationInfoPane

    OpenCurrentResultsAction() {
        super('OpenCurrentResults')
    }

    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            Simulation simulation = simulationInfoPane.simulationInfoPaneModel.latestFinishedSimulation
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(UIItemFactory.createItem(simulation)))
        }
    }

    @Override
    boolean isEnabled() {
        simulationInfoPane.simulationInfoPaneModel.latestFinishedSimulation
    }
}
