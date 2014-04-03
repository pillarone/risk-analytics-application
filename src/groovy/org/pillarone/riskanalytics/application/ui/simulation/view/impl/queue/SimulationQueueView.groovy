package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.ULCTable
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationQueueViewModel
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class SimulationQueueView {

    @Resource
    SimulationQueueViewModel simulationQueueViewModel
    @Resource
    QueueContextMenu queueContextMenu
    @Resource
    SimulationInfoPane simulationInfoPane

    private ULCBoxPane content
    private ULCTable queueTable

    @PostConstruct
    void initialize() {
        this.content = new ULCBoxPane(1, 2)
        queueTable = new ULCTable(simulationQueueViewModel.simulationQueueTableModel)
        queueTable.componentPopupMenu = queueContextMenu
        def infoContent = simulationInfoPane.content
        infoContent.border = BorderFactory.createTitledBorder("Simulation Information")
        content.add(ULCBoxPane.BOX_EXPAND_TOP, infoContent)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(queueTable))
    }

    ULCComponent getContent() {
        content
    }

    int getSelectedRow() {
        queueTable.selectedRow
    }
}
