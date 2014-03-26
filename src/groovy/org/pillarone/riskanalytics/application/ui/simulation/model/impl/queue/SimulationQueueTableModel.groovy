package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.table.AbstractTableModel
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo

class SimulationQueueTableModel extends AbstractTableModel {

    private List<SimulationRuntimeInfo> queueItems = []

    SimulationQueueTableModel(List<SimulationRuntimeInfo> infos) {
        this.queueItems.addAll(infos)
    }

    @Override
    int getRowCount() {
        queueItems.size()
    }

    @Override
    int getColumnCount() {
        return 8
    }

    @Override
    String getColumnName(int column) {
        switch (column) {
            case 0: return 'Simulation'
            case 1: return 'P14n'
            case 2: return 'Template'
            case 3: return 'Iterations'
            case 4: return 'Priority'
            case 5: return 'Configured Ar'
            case 6: return 'State'
            case 7: return 'Progress'
            default: throw new IllegalStateException("wrong column index: $column")
        }
    }

    @Override
    Object getValueAt(int row, int column) {
        SimulationRuntimeInfo item = queueItems[row]
        switch (column) {
            case 0: return item.simulation.nameAndVersion
            case 1: return item.p14n
            case 2: return item.resultConfiguration
            case 3: return item.iterations?.toString()
            case 4: return item.priority?.toString()
            case 5: return item.configuredAt
            case 6: return item.simulationState.toString()
            case 7: return item.progress.toString()
            default: throw new IllegalStateException("wrong column index: $column")
        }
    }

    void addItem(SimulationRuntimeInfo item, int index) {
        queueItems.add(index, item)
        fireTableRowsInserted(index, index)
    }

    void removeItem(UUID id) {
        def find = queueItems.find { it.id == id }
        def index = queueItems.indexOf(find)
        queueItems.remove(index)
        fireTableRowsDeleted(index, index)
    }

    void itemChanged(SimulationRuntimeInfo info) {
        def index = queueItems.indexOf(info)
        fireTableRowsUpdated(index, index)
    }
}
