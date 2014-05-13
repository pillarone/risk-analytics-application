package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.action

import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SimulationQueueView
import org.pillarone.riskanalytics.core.simulation.engine.SimulationQueueService
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class CancelSimulationAction extends ResourceBasedAction {
    private static Log LOG = LogFactory.getLog(CancelSimulationAction)

    @Resource
    SimulationQueueService simulationQueueService
    @Resource
    SimulationQueueView simulationQueueView

    CancelSimulationAction() {
        super('CancelSimulation')
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            simulationQueueView.selectedSimulations.each {
                LOG.info("Canceling queued sim: $it")
                simulationQueueService.cancel(it.id)
            }
        }
    }

    @Override
    boolean isEnabled() {
        simulationQueueView.selectedSimulations.size() > 0
    }
}
