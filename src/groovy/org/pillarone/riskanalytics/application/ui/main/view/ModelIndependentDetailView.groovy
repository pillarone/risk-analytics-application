package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SimulationQueueView

import javax.annotation.PostConstruct

class ModelIndependentDetailView {

    private ULCDetachableTabbedPane tabbedPane
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
