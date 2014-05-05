package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.table.ULCTableColumn
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationQueueViewModel
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
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
class SimulationQueueView {

    @Resource
    SimulationQueueViewModel simulationQueueViewModel
    @Resource
    SimulationInfoPane simulationInfoPane

    @Resource
    QueueTableRenderer queueTableRenderer

    private ULCBoxPane content
    private ULCTable queueTable

    private IListSelectionListener updateMenuListener

    @PostConstruct
    void initialize() {
        content = new ULCBoxPane(1, 2)
        queueTable = new ULCTable(simulationQueueViewModel.simulationQueueTableModel)
        updateMenuListener = { ListSelectionEvent event ->
            queueTableRenderer.updateMenuEnablingState()
        } as IListSelectionListener
        queueTable.selectionModel.addListSelectionListener(updateMenuListener)
        queueTable.columnModel.columns.each { ULCTableColumn column ->
            column.cellRenderer = queueTableRenderer
        }
        ULCBoxPane infoContent = simulationInfoPane.content
        infoContent.border = BorderFactory.createTitledBorder('Simulation Information')
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

    List<SimulationRuntimeInfo> getSelectedSimulations() {
        simulationQueueViewModel.getInfoAt(queueTable.selectedRows)
    }
}
