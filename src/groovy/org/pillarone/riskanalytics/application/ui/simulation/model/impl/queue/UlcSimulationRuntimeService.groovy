package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.PollingSupport
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.core.simulation.engine.ISimulationRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfoEvent
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
    @Resource(name = 'pollingSupport2000')
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
            events.each { fireSimulationInfoEvent(it) }
            events.clear()
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

    private class MyActionListener implements IActionListener {
        @Override
        void actionPerformed(ActionEvent event) {
            fireEvents()
        }
    }
}
