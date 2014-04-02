package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SimulationQueueView
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class ModelIndependentDetailView {

    private ULCDetachableTabbedPane tabbedPane
    @Resource
    SimulationQueueView simulationQueueView

    @PostConstruct
    void initialize() {
        tabbedPane = new ULCDetachableTabbedPane()
        tabbedPane.addTab("Simulation Queue", simulationQueueView.content)
        def tab = tabbedPane.indexOfTab("Simulation Queue")
        tabbedPane.setCloseableTab(tab, false)
    }

    ULCComponent getContent() {
        return tabbedPane
    }
}
