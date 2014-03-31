package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.pillarone.riskanalytics.core.simulation.engine.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope('ulcSessionScope')
@Component
class SimulationQueueViewModel {
    @Resource
    SimulationQueueEventService simulationQueueEventService
    @Autowired
    SimulationRuntimeService simulationRuntimeService
    @Resource
    SimulationQueueTableModel simulationQueueTableModel

    @PostConstruct
    void initialize() {
        simulationQueueTableModel.queueItems = simulationRuntimeService.queued
    }

    void update() {
        List<SimulationRuntimeInfoEvent> events = simulationQueueEventService.poll()
        events.each { SimulationRuntimeInfoEvent event ->
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
