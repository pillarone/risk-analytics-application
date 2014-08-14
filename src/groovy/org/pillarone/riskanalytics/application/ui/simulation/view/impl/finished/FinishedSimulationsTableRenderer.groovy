package org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.finished.FinishedSimulationsViewModel
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@CompileStatic
@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedSimulationsTableRenderer extends DefaultTableCellRenderer {

    @Resource
    GrailsApplication grailsApplication

    @Resource
    FinishedSimulationsViewModel finishedSimulationsViewModel

    @Lazy
    private FinishedSimulationsContextMenu contextMenu = grailsApplication.mainContext.getBean('finishedSimulationsContextMenu', FinishedSimulationsContextMenu)

    void updateMenuEnablingState() {
        contextMenu.updateEnablingState()
    }

    @Override
    IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean selected, boolean hasFocus, int row) {
        IRendererComponent component = super.getTableCellRendererComponent(table, value, selected, hasFocus, row)
        SimulationRuntimeInfo info = finishedSimulationsViewModel.getInfoAt([row] as int[])?.first()
        enabled = !info.deleted
        componentPopupMenu = contextMenu
        horizontalAlignment = LEFT
        return component
    }

}
