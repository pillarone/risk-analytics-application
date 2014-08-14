package org.pillarone.riskanalytics.application.ui.simulation.model.impl.finished
import com.google.common.eventbus.Subscribe
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.eventbus.RiskAnalyticsEventBus
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.ModellingItemEvent
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.UlcSimulationRuntimeService
import org.pillarone.riskanalytics.core.queue.IRuntimeInfoListener
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfoAdapter
import org.pillarone.riskanalytics.core.simulation.item.Simulation
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
        riskAnalyticsEventBus.register(this)
    }

    @PreDestroy
    void unregister() {
        riskAnalyticsEventBus.unregister(this)
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

    @Subscribe
    void onEvent(ModellingItemEvent event) {
        if (!(event.modellingItem instanceof Simulation)) {
            return
        }
        Simulation simulation = event.modellingItem as Simulation
        switch (event.eventType) {
            case CacheItemEvent.EventType.ADDED:
                break
            case CacheItemEvent.EventType.REMOVED:
                finishedSimulationsTableModel.simulationDeleted(simulation)
                break
            case CacheItemEvent.EventType.UPDATED:
                break
        }
    }

    private class MyInfoListener extends SimulationRuntimeInfoAdapter {
        @Override
        void finished(SimulationRuntimeInfo info) {
            finishedSimulationsTableModel.itemAdded(info)
        }
    }
}
