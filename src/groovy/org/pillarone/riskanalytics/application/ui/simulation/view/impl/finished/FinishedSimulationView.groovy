package org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.table.ULCTableColumn
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.finished.FinishedSimulationsViewModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.action.OpenResultsAction
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedSimulationView {

    @Resource
    FinishedSimulationsViewModel finishedSimulationsViewModel

    @Resource
    FinishedSimulationsTableRenderer finishedSimulationsTableRenderer

    @Resource
    GrailsApplication grailsApplication

    private ULCBoxPane content
    private ULCTable finishedSimulationsTable

    private IListSelectionListener updateMenuListener

    @PostConstruct
    void initialize() {
        this.content = new ULCBoxPane(1, 1)
        finishedSimulationsTable = new ULCTable(finishedSimulationsViewModel.finishedSimulationsTableModel)
        finishedSimulationsTable.addActionListener({
            grailsApplication.mainContext.getBean('openResultsAction', OpenResultsAction).doActionPerformed(null)
        } as IActionListener)
        updateMenuListener = { ListSelectionEvent event ->
            finishedSimulationsTableRenderer.updateMenuEnablingState()
        } as IListSelectionListener
        finishedSimulationsTable.selectionModel.addListSelectionListener(updateMenuListener)
        finishedSimulationsTable.columnModel.columns.each { ULCTableColumn column ->
            column.cellRenderer = finishedSimulationsTableRenderer
        }
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(finishedSimulationsTable))
    }

    @PreDestroy
    void close() {
        finishedSimulationsTable.selectionModel.removeListSelectionListener(updateMenuListener)
        updateMenuListener = null
    }

    ULCComponent getContent() {
        content
    }

    List<SimulationRuntimeInfo> getSelectedSimulations() {
        finishedSimulationsViewModel.getInfoAt(finishedSimulationsTable.selectedRows)
    }

    void removeSelected() {
        if (finishedSimulationsTable.selectedRows) {
            finishedSimulationsViewModel.removeAt(finishedSimulationsTable.selectedRows)
        }
    }
}
