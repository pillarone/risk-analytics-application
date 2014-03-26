package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.pillarone.riskanalytics.core.simulation.engine.*

import javax.annotation.PostConstruct

class SimulationQueueViewModel {
    SimulationQueueEventService simulationQueueEventService
    SimulationRuntimeService simulationRuntimeService
    SimulationQueueTableModel queueTableModel

    @PostConstruct
    void initialize() {
        queueTableModel = new SimulationQueueTableModel(simulationRuntimeService.queued)
    }

    void update() {
        List<SimulationRuntimeInfoEvent> events = simulationQueueEventService.poll()
        events.each { SimulationRuntimeInfoEvent event ->
            switch (event.class) {
                case (AddSimulationRuntimeInfoEvent):
                    AddSimulationRuntimeInfoEvent addEvent = event as AddSimulationRuntimeInfoEvent
                    queueTableModel.addItem(event.info, addEvent.index)
                    break;
                case (DeleteSimulationRuntimeInfoEvent):
                    queueTableModel.removeItem(event.info.id)
                    break;
                case (ChangeSimulationRuntimeInfoEvent):
                    queueTableModel.itemChanged(event.info)
            }
        }
    }
}
