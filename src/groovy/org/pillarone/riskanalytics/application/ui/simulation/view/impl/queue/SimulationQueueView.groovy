package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.ULCTable
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationQueueViewModel

import javax.annotation.PostConstruct

class SimulationQueueView {

    SimulationQueueViewModel simulationQueueViewModel
    private ULCScrollPane content

    @PostConstruct
    void initialize() {
        ULCScrollPane content = new ULCScrollPane()
        ULCTable queueTable = new ULCTable(simulationQueueViewModel.queueTableModel)
        content.add(queueTable)
    }

    ULCComponent getContent() {
        content
    }
}
