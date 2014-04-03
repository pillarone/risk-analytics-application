package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.RealTimeLoggingView
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SimulationQueueView
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class ModelIndependentDetailView {

    private static final String SIMULATION_QUEUE_TAB_NAME = "Simulation Queue"
    private static final String SIMULATION_LOGGING_TAB_NAME = 'Simulation Logging'
    private ULCDetachableTabbedPane tabbedPane
    @Resource
    SimulationQueueView simulationQueueView

    @Resource
    RealTimeLoggingView realTimeLoggingView

    @PostConstruct
    void initialize() {
        tabbedPane = new ULCDetachableTabbedPane()
        tabbedPane.addTab(SIMULATION_QUEUE_TAB_NAME, simulationQueueView.content)
        tabbedPane.setCloseableTab(tabbedPane.indexOfTab(SIMULATION_QUEUE_TAB_NAME), false)

        tabbedPane.addTab(SIMULATION_LOGGING_TAB_NAME, realTimeLoggingView.content)
        tabbedPane.setCloseableTab(tabbedPane.indexOfTab(SIMULATION_LOGGING_TAB_NAME), false)
    }

    ULCComponent getContent() {
        return tabbedPane
    }
}
