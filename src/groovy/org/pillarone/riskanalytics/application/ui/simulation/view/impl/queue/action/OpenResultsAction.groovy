package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.action

import com.ulcjava.base.application.event.ActionEvent
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SimulationQueueView
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class OpenResultsAction extends ResourceBasedAction {

    @Resource
    GrailsApplication grailsApplication

    OpenResultsAction() {
        super("OpenResults");
    }

    void doActionPerformed(ActionEvent event) {
        trace("Open result for simulation: ${selectedInfo?.simulation}")
        simulationQueueView.simulationQueueViewModel.openResultAt(simulationQueueView.selectedRow)
    }

    private SimulationQueueView getSimulationQueueView() {
        grailsApplication.mainContext.getBean('simulationQueueView', SimulationQueueView)
    }

    private SimulationRuntimeInfo getSelectedInfo() {
        simulationQueueView.simulationQueueViewModel.getSimulationRuntimeInfoAt(simulationQueueView.selectedRow)
    }

    @Override
    boolean isEnabled() {
        return super.isEnabled()
//        (selectedInfo?.simulationState == SimulationState.FINISHED) && super.enabled
    }
}
