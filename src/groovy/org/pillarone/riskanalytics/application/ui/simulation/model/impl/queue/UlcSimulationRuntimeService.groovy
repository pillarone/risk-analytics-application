package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.PollingSupport
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.core.queue.IRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.queue.RuntimeInfoEventSupport
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeService
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class UlcSimulationRuntimeService {

    @Resource
    SimulationRuntimeService simulationRuntimeService
    @Resource(name = 'pollingSupport2000')
    PollingSupport pollingSupport
    private MyRuntimeEventListener runtimeEventListener
    private IActionListener pollingListener
    private List<SimulationRuntimeInfoEvent> events = []
    private final Object eventsLock = new Object()
    @Delegate
    private final RuntimeInfoEventSupport eventSupport = new RuntimeInfoEventSupport()

    @PostConstruct
    void register() {
        pollingListener = new MyActionListener()
        runtimeEventListener = new MyRuntimeEventListener()
        simulationRuntimeService.addRuntimeInfoListener(runtimeEventListener)
        pollingSupport.addActionListener(pollingListener)
    }

    @PreDestroy
    private void unregister() {
        pollingSupport.removeActionListener(pollingListener)
        simulationRuntimeService.removeRuntimeInfoListener(runtimeEventListener)
    }


    private void fireEvents() {
        List currentEvents
        synchronized (eventsLock) {
            currentEvents = events
            events = []
        }
        currentEvents.each { SimulationRuntimeInfoEvent event ->
            SimulationRuntimeInfo info = event.info
            switch (event.type) {
                case SimulationRuntimeInfoEvent.TYPE.OFFERED:
                    offered(info)
                    break
                case SimulationRuntimeInfoEvent.TYPE.STARTING:
                    starting(info)
                    break
                case SimulationRuntimeInfoEvent.TYPE.FINISHED:
                    finished(info)
                    break
                case SimulationRuntimeInfoEvent.TYPE.REMOVED:
                    removed(info)
                    break
                case SimulationRuntimeInfoEvent.TYPE.CHANGED:
                    changed(info)
                    break
            }
        }
    }

    private class MyRuntimeEventListener implements IRuntimeInfoListener<SimulationRuntimeInfo> {
        @Override
        void starting(SimulationRuntimeInfo info) {
            synchronized (eventsLock) {
                events << new SimulationRuntimeInfoEvent(type: SimulationRuntimeInfoEvent.TYPE.STARTING, info: info)
            }
        }

        @Override
        void finished(SimulationRuntimeInfo info) {
            synchronized (eventsLock) {
                events << new SimulationRuntimeInfoEvent(type: SimulationRuntimeInfoEvent.TYPE.FINISHED, info: info)
            }

        }

        @Override
        void removed(SimulationRuntimeInfo info) {
            synchronized (eventsLock) {
                events << new SimulationRuntimeInfoEvent(type: SimulationRuntimeInfoEvent.TYPE.REMOVED, info: info)
            }
        }

        @Override
        void offered(SimulationRuntimeInfo info) {
            synchronized (eventsLock) {
                events << new SimulationRuntimeInfoEvent(type: SimulationRuntimeInfoEvent.TYPE.OFFERED, info: info)
            }
        }

        @Override
        void changed(SimulationRuntimeInfo info) {
            synchronized (eventsLock) {
                events << new SimulationRuntimeInfoEvent(type: SimulationRuntimeInfoEvent.TYPE.CHANGED, info: info)
            }
        }
    }

    private class MyActionListener implements IActionListener {
        @Override
        void actionPerformed(ActionEvent event) {
            fireEvents()
        }
    }

    static class SimulationRuntimeInfoEvent {
        enum TYPE {
            OFFERED, STARTING, FINISHED, REMOVED, CHANGED
        }
        SimulationRuntimeInfo info
        TYPE type
    }

}
