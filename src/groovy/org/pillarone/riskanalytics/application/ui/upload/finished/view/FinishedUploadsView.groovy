package org.pillarone.riskanalytics.application.ui.upload.finished.view

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
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.action.OpenResultsAction
import org.pillarone.riskanalytics.application.ui.upload.finished.model.FinishedUploadsViewModel
import org.pillarone.riskanalytics.core.upload.UploadRuntimeInfo
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedUploadsView {

    @Resource
    FinishedUploadsViewModel finishedUploadsViewModel

    @Resource
    FinishedUploadsTableRenderer finishedUploadsTableRenderer

    @Resource
    GrailsApplication grailsApplication

    private ULCBoxPane content
    private ULCTable finishedUploadsTable

    private IListSelectionListener updateMenuListener

    @PostConstruct
    void initialize() {
        this.content = new ULCBoxPane(1, 1)
        finishedUploadsTable = new ULCTable(finishedUploadsViewModel.finishedUploadsTableModel)
        finishedUploadsTable.addActionListener({
            grailsApplication.mainContext.getBean('openResultsAction', OpenResultsAction).doActionPerformed(null)
        } as IActionListener)
        updateMenuListener = { ListSelectionEvent event ->
            finishedUploadsTableRenderer.updateMenuEnablingState()
        } as IListSelectionListener
        finishedUploadsTable.selectionModel.addListSelectionListener(updateMenuListener)
        finishedUploadsTable.columnModel.columns.each { ULCTableColumn column ->
            column.cellRenderer = finishedUploadsTableRenderer
        }
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(finishedUploadsTable))
    }

    @PreDestroy
    void close() {
        finishedUploadsTable.selectionModel.removeListSelectionListener(updateMenuListener)
        updateMenuListener = null
    }

    ULCComponent getContent() {
        content
    }

    List<UploadRuntimeInfo> getSelectedSimulations() {
        finishedUploadsViewModel.getInfoAt(finishedUploadsTable.selectedRows)
    }

    void removeSelected() {
        if (finishedUploadsTable.selectedRows) {
            finishedUploadsViewModel.removeAt(finishedUploadsTable.selectedRows)
        }
    }
}
