package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.core.simulation.engine.*
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
        simulationQueueTableModel.queueItems = simulationRuntimeService.queued
    }

    @PreDestroy
    void unregister() {
        ulcSimulationRuntimeService.removeSimulationRuntimeInfoListener(infoListener)
    }

    void cancelAt(int index) {
        SimulationRuntimeInfo info = simulationQueueTableModel.getInfoAt(index)
        simulationQueueService.cancel(info.id)
    }

    private class MyInfoListener implements ISimulationRuntimeInfoListener {
        @Override
        void onEvent(SimulationRuntimeInfoEvent event) {
            update(event)
        }

        private void update(SimulationRuntimeInfoEvent event) {
            switch (event.class) {
                case (AddSimulationRuntimeInfoEvent):
                    AddSimulationRuntimeInfoEvent addEvent = event as AddSimulationRuntimeInfoEvent
                    simulationQueueTableModel.itemAdded(event.info, addEvent.index)
                    break
                case (DeleteSimulationRuntimeInfoEvent):
                    simulationQueueTableModel.itemRemoved(event.info.id)
                    break
                case (ChangeSimulationRuntimeInfoEvent):
                    simulationQueueTableModel.itemChanged(event.info)
                    break
                default: return
            }
        }
    }
}
