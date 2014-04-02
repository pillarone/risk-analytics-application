package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.action

import com.ulcjava.base.application.event.ActionEvent
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SimulationQueueView
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class CancelSimulationAction extends ResourceBasedAction {

    @Resource
    GrailsApplication grailsApplication

    CancelSimulationAction() {
        super('CancelSimulation')
    }

    SimulationQueueView getSimulationQueueView() {
        grailsApplication.mainContext.getBean('simulationQueueView', SimulationQueueView)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        def row = simulationQueueView.queueTable.selectedRow
        if (row != -1) {
            simulationQueueView.simulationQueueViewModel.cancelAt(row)
        }
    }
}
