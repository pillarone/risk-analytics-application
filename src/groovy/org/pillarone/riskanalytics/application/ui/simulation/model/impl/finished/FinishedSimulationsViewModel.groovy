package org.pillarone.riskanalytics.application.ui.simulation.model.impl.finished

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.eventbus.RiskAnalyticsEventBus
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.UlcSimulationRuntimeService
import org.pillarone.riskanalytics.core.queue.IRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfoAdapter
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
    RiskAnalyticsEventBus riskAnalyticsEventBus
    @Resource
    FinishedSimulationsTableModel finishedSimulationsTableModel

    private final IRuntimeInfoListener infoListener = new MyInfoListener()

    @PostConstruct
    void initialize() {
        ulcSimulationRuntimeService.addRuntimeInfoListener(infoListener)
    }

    @PreDestroy
    void unregister() {
        ulcSimulationRuntimeService.removeRuntimeInfoListener(infoListener)
    }

    List<SimulationRuntimeInfo> getInfoAt(int[] selected) {
        selected.collect {
            finishedSimulationsTableModel.getInfoAt(it)
        }
    }

    void clearAll() {
        finishedSimulationsTableModel.infos = []
    }

    void removeAt(int[] selected) {
        finishedSimulationsTableModel.removeAt(selected)
    }

    private class MyInfoListener extends SimulationRuntimeInfoAdapter {
        @Override
        void finished(SimulationRuntimeInfo info) {
            finishedSimulationsTableModel.itemAdded(info)
        }
    }
}
