package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.engine.ISimulationRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfoAdapter
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeService
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.user.UserManagement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedSimulationsViewModel {
    @Resource
    UlcSimulationRuntimeService ulcSimulationRuntimeService
    @Autowired
    SimulationRuntimeService simulationRuntimeService
    @Resource
    FinishedSimulationsTableModel finishedSimulationsTableModel
    @Resource
    RiskAnalyticsMainModel riskAnalyticsMainModel

    private final ISimulationRuntimeInfoListener infoListener = new MyInfoListener()

    @PostConstruct
    void initialize() {
        ulcSimulationRuntimeService.addSimulationRuntimeInfoListener(infoListener)
        finishedSimulationsTableModel.infos = simulationRuntimeService.finished
    }

    @PreDestroy
    void unregister() {
        ulcSimulationRuntimeService.removeSimulationRuntimeInfoListener(infoListener)
    }

    void openResultAt(int index) {
        if (index != -1) {
            SimulationRuntimeInfo info = finishedSimulationsTableModel.getInfoAt(index)
            if (info.simulationState == SimulationState.FINISHED) {
                Simulation simulation = info.simulation
                simulation.load()
                riskAnalyticsMainModel.notifyOpenDetailView((Model) simulation.modelClass.newInstance(), simulation)
            }
        }
    }

    SimulationRuntimeInfo getSimulationRuntimeInfoAt(int index) {
        index != -1 ? finishedSimulationsTableModel.getInfoAt(index) : null
    }

    private class MyInfoListener extends SimulationRuntimeInfoAdapter {

        @Override
        void finished(SimulationRuntimeInfo info) {
            if (isMine(info)) {
                finishedSimulationsTableModel.itemAdded(info)
            }
        }

        private boolean isMine(SimulationRuntimeInfo info) {
            UserManagement.currentUser?.username == info.offeredBy?.username
        }
    }
}
