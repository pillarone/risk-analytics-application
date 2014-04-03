package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.table.AbstractTableModel
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class SimulationQueueTableModel extends AbstractTableModel {

    protected final List<SimulationRuntimeInfo> queueItems = []

    @Override
    int getRowCount() {
        queueItems.size()
    }

    @Override
    int getColumnCount() {
        return 8
    }

    SimulationRuntimeInfo getInfoAt(int index) {
        queueItems[index]
    }

    @Override
    String getColumnName(int column) {
        //TODO define the exact columns and move to resource bundle
        switch (column) {
            case 0: return 'Simulation'
            case 1: return 'P14n'
            case 2: return 'Template'
            case 3: return 'Iterations'
            case 4: return 'Priority'
            case 5: return 'Configured At'
            case 6: return 'Configured By'
            case 7: return 'State'
            default: throw new IllegalStateException("wrong column index: $column")
        }
    }

    @Override
    String getValueAt(int row, int column) {
        //TODO register proper DataTypes on the cells
        SimulationRuntimeInfo item = queueItems[row]
        switch (column) {
            case 0: return item.simulation.nameAndVersion
            case 1: return item.parameterization.nameAndVersion
            case 2: return item.resultConfiguration.nameAndVersion
            case 3: return item.iterations?.toString()
            case 4: return item.priority?.toString()
            case 5: return item.configuredAt.toString()
            case 6: return item.offeredBy?.username
            case 7: return item.simulationState.toString()
            default: throw new IllegalStateException("wrong column index: $column")
        }
    }

    void setQueueItems(List<SimulationRuntimeInfo> queueItems) {
        this.queueItems.clear()
        this.queueItems.addAll(queueItems)
        fireTableDataChanged()
    }

    void itemAdded(SimulationRuntimeInfo item) {
        queueItems.add(item)
        queueItems.sort()
        fireTableDataChanged()
    }

    void itemRemoved(SimulationRuntimeInfo info) {
        def index = queueItems.indexOf(info)
        if (index != -1) {
            queueItems.remove(index)
            fireTableRowsDeleted(index, index)
        }
    }

    void itemChanged(SimulationRuntimeInfo info) {
        def index = queueItems.indexOf(info)
        if (index != -1) {
            //only update cells which can change:
            fireTableCellUpdated(index, 7)
            fireTableCellUpdated(index, 8)
        }
    }
}
