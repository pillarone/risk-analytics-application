package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent

class ModelIndependentDetailView {

    private ULCDetachableTabbedPane tabbedPane

    ModelIndependentDetailView() {
        tabbedPane = new ULCDetachableTabbedPane()
        tabbedPane.addTab("Simulation Queue", new ULCBoxPane())
        def tab = tabbedPane.indexOfTab("Simulation Queue")
        tabbedPane.setCloseableTab(tab, false)
    }

    ULCComponent getContent() {
        return tabbedPane
    }
}
