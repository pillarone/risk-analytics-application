package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.pillarone.riskanalytics.core.simulation.engine.ISimulationRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfoEvent
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeService

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

class SimulationQueueEventService {

    SimulationRuntimeService simulationRuntimeService
    private MySimulationRuntimeEventListener runtimeEventListener
    private final List<SimulationRuntimeInfoEvent> events = []

    @PostConstruct
    void register() {
        runtimeEventListener = new MySimulationRuntimeEventListener()
        simulationRuntimeService.addListener(runtimeEventListener)
    }

    @PreDestroy
    void unregister() {
        simulationRuntimeService.removeListener(runtimeEventListener)
    }

    List<SimulationRuntimeInfoEvent> poll() {
        synchronized (events) {
            def result = new ArrayList<SimulationRuntimeInfoEvent>(events)
            this.events.clear()
            result
        }
    }

    private class MySimulationRuntimeEventListener implements ISimulationRuntimeInfoListener {
        @Override
        void onEvent(SimulationRuntimeInfoEvent event) {
            synchronized (events) {
                events << event
            }
        }
    }
}
