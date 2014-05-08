package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.core.simulation.engine.ISimulationRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.engine.SimulationQueueService
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class SimulationQueueViewModel {
    @Resource
    UlcSimulationRuntimeService ulcSimulationRuntimeService
    @Autowired
    SimulationRuntimeService simulationRuntimeService
    @Resource
    SimulationQueueTableModel simulationQueueTableModel
    @Autowired
    SimulationQueueService simulationQueueService

    private final ISimulationRuntimeInfoListener infoListener = new MyInfoListener()

    @PostConstruct
    void initialize() {
        ulcSimulationRuntimeService.addSimulationRuntimeInfoListener(infoListener)
        simulationQueueTableModel.infos = simulationRuntimeService.queued
    }

    @PreDestroy
    void unregister() {
        ulcSimulationRuntimeService.removeSimulationRuntimeInfoListener(infoListener)
    }

    List<SimulationRuntimeInfo> getInfoAt(int[] selected) {
        selected.collect {
            simulationQueueTableModel.getInfoAt(it)
        }
    }

    private class MyInfoListener implements ISimulationRuntimeInfoListener {

        @Override
        void starting(SimulationRuntimeInfo info) {
            simulationQueueTableModel.itemChanged(info)

        }

        @Override
        void finished(SimulationRuntimeInfo info) {
            simulationQueueTableModel.itemRemoved(info)

        }

        @Override
        void removed(SimulationRuntimeInfo info) {
            simulationQueueTableModel.itemRemoved(info)

        }

        @Override
        void offered(SimulationRuntimeInfo info) {
            simulationQueueTableModel.itemAdded(info)

        }

        @Override
        void changed(SimulationRuntimeInfo info) {
            simulationQueueTableModel.itemChanged(info)
        }
    }
}
