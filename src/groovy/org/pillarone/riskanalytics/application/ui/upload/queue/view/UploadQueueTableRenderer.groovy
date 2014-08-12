package org.pillarone.riskanalytics.application.ui.upload.queue.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@CompileStatic
@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class UploadQueueTableRenderer extends DefaultTableCellRenderer {

    @Resource
    GrailsApplication grailsApplication

    @Lazy
    private UploadQueueContextMenu queueContextMenu = grailsApplication.mainContext.getBean('uploadQueueContextMenu', UploadQueueContextMenu)

    void updateMenuEnablingState() {
        queueContextMenu.updateEnablingState()
    }

    @Override
    IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean selected, boolean hasFocus, int row) {
        IRendererComponent component = super.getTableCellRendererComponent(table, value, selected, hasFocus, row)
        toolTipText = String.valueOf(value)
        componentPopupMenu = queueContextMenu
        horizontalAlignment = LEFT
        return component
    }

}
