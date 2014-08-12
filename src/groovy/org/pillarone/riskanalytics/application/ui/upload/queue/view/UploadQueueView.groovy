package org.pillarone.riskanalytics.application.ui.upload.queue.view

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.table.ULCTableColumn
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.upload.queue.model.UploadQueueViewModel
import org.pillarone.riskanalytics.core.upload.UploadRuntimeInfo
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

import static com.ulcjava.base.shared.IDefaults.BOX_EXPAND_EXPAND
import static com.ulcjava.base.shared.IDefaults.BOX_EXPAND_TOP

@CompileStatic
@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class UploadQueueView {

    @Resource
    UploadQueueViewModel uploadQueueViewModel
    @Resource
    UploadInfoPane uploadInfoPane

    @Resource
    UploadQueueTableRenderer uploadQueueTableRenderer

    private ULCBoxPane content
    private ULCTable queueTable

    private IListSelectionListener updateMenuListener

    @PostConstruct
    void initialize() {
        content = new ULCBoxPane(1, 2)
        queueTable = new ULCTable(uploadQueueViewModel.uploadQueueTableModel)
        updateMenuListener = { ListSelectionEvent event ->
            uploadQueueTableRenderer.updateMenuEnablingState()
        } as IListSelectionListener
        queueTable.selectionModel.addListSelectionListener(updateMenuListener)
        queueTable.columnModel.columns.each { ULCTableColumn column ->
            column.cellRenderer = uploadQueueTableRenderer
        }
        ULCBoxPane infoContent = uploadInfoPane.content
        infoContent.border = BorderFactory.createTitledBorder('Upload Information')
        content.add(BOX_EXPAND_TOP, infoContent)
        content.add(BOX_EXPAND_EXPAND, new ULCScrollPane(queueTable))
    }

    @PreDestroy
    void close() {
        queueTable.selectionModel.removeListSelectionListener(updateMenuListener)
        updateMenuListener = null
    }

    ULCComponent getContent() {
        content
    }

    List<UploadRuntimeInfo> getSelectedUploads() {
        uploadQueueViewModel.getInfoAt(queueTable.selectedRows)
    }
}
