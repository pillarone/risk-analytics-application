package org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished

import com.google.common.eventbus.Subscribe
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
import org.pillarone.riskanalytics.application.ui.main.eventbus.RiskAnalyticsEventBus
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.ModellingItemEvent
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.finished.FinishedSimulationsViewModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.action.OpenResultsAction
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedSimulationView {

    @Autowired
    RiskAnalyticsEventBus riskAnalyticsEventBus

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
        riskAnalyticsEventBus.register(this)
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
        riskAnalyticsEventBus.unregister(this)
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

    @Subscribe
    void onEvent(ModellingItemEvent event) {
        if (!(event.modellingItem instanceof Simulation)) {
            return
        }
        Simulation simulation = event.modellingItem as Simulation
        switch (event.eventType) {
            case CacheItemEvent.EventType.ADDED:
                break
            case CacheItemEvent.EventType.REMOVED:
                finishedSimulationsViewModel.simulationDeleted(simulation)
                //Because selection does not change, we have to trigger the update manually
                finishedSimulationsTableRenderer.updateMenuEnablingState()
                break
            case CacheItemEvent.EventType.UPDATED:
                break
        }
    }
}
