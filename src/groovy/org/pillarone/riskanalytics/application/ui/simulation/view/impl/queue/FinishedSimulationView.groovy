package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.FinishedSimulationsViewModel
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedSimulationView {

    @Resource
    FinishedSimulationsViewModel finishedSimulationsViewModel
    @Resource
    FinishedSimulationsContextMenu finishedSimulationsContextMenu

    private ULCBoxPane content
    private ULCTable finishedSimulationsTable

    @PostConstruct
    void initialize() {
        this.content = new ULCBoxPane(1, 1)
        finishedSimulationsTable = new ULCTable(finishedSimulationsViewModel.finishedSimulationsTableModel)
        finishedSimulationsTable.componentPopupMenu = finishedSimulationsContextMenu
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(finishedSimulationsTable))
    }

    ULCComponent getContent() {
        content
    }

    int getSelectedRow() {
        finishedSimulationsTable.selectedRow
    }
}
