package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.PollingSupport
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.core.simulation.engine.ISimulationRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfoEventSupport
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class UlcSimulationRuntimeService {

    @Autowired
    SimulationRuntimeService simulationRuntimeService
    @Resource(name = 'pollingSupport1000')
    PollingSupport pollingSupport
    private MySimulationRuntimeEventListener runtimeEventListener
    private IActionListener pollingListener
    private final List<SimulationRuntimeInfoEvent> events = []
    @Delegate
    private final SimulationRuntimeInfoEventSupport eventSupport = new SimulationRuntimeInfoEventSupport()

    @PostConstruct
    private void register() {
        pollingListener = new MyActionListener()
        runtimeEventListener = new MySimulationRuntimeEventListener()
        simulationRuntimeService.addSimulationRuntimeInfoListener(runtimeEventListener)
        pollingSupport.addActionListener(pollingListener)
    }

    @PreDestroy
    private void unregister() {
        pollingSupport.removeActionListener(pollingListener)
        simulationRuntimeService.removeSimulationRuntimeInfoListener(runtimeEventListener)
    }

    private void fireEvents() {
        synchronized (events) {
            events.each { SimulationRuntimeInfoEvent event ->
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
            events.clear()
        }
    }

    private class MySimulationRuntimeEventListener implements ISimulationRuntimeInfoListener {
        @Override
        void starting(SimulationRuntimeInfo info) {
            events << new SimulationRuntimeInfoEvent(type: SimulationRuntimeInfoEvent.TYPE.STARTING, info: info)
        }

        @Override
        void finished(SimulationRuntimeInfo info) {
            events << new SimulationRuntimeInfoEvent(type: SimulationRuntimeInfoEvent.TYPE.FINISHED, info: info)

        }

        @Override
        void removed(SimulationRuntimeInfo info) {
            events << new SimulationRuntimeInfoEvent(type: SimulationRuntimeInfoEvent.TYPE.REMOVED, info: info)

        }

        @Override
        void offered(SimulationRuntimeInfo info) {
            events << new SimulationRuntimeInfoEvent(type: SimulationRuntimeInfoEvent.TYPE.OFFERED, info: info)

        }

        @Override
        void changed(SimulationRuntimeInfo info) {
            events << new SimulationRuntimeInfoEvent(type: SimulationRuntimeInfoEvent.TYPE.CHANGED, info: info)

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
