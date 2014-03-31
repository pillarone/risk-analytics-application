package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.pillarone.riskanalytics.core.simulation.engine.ISimulationRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfoEvent
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Scope('ulcSessionScope')
@Component
class SimulationQueueEventService {

    @Autowired
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
