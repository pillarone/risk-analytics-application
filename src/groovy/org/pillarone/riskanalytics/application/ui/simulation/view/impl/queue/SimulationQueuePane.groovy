package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCScrollPane
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationQueuePaneModel

class SimulationQueuePane {

    private ULCBoxPane content

    private SortableTable simulationTable

    private final SimulationQueuePaneModel model

    SimulationQueuePane(SimulationQueuePaneModel model) {
        this.model = model
        initialize()
    }

    private initialize() {
        content = new ULCBoxPane()
        simulationTable = new SortableTable(model.simulationTableModel)
        ULCScrollPane scrollPane = new ULCScrollPane()
        scrollPane.verticalScrollBar.blockIncrement = 100
        scrollPane.add(simulationTable)
        content.add(scrollPane)
    }

    ULCComponent getContent() {
        content
    }
}
