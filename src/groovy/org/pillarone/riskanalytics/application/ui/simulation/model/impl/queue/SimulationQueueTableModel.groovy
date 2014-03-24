package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.table.AbstractTableModel

class SimulationQueueTableModel extends AbstractTableModel {

    private List<IQueueItem> queueItems = []

    SimulationQueueTableModel(List<IQueueItem> queueItems) {
        this.queueItems.addAll(queueItems)
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
    Object getValueAt(int row, int column) {

        IQueueItem item = queueItems[row]
        switch (column) {
            case 0: return item.simulation?.toString()
            case 1: return item.batchRun?.toString()
            case 2: return item.p14n?.toString()
            case 3: return item.resultConfiguration?.toString()
            case 4: return item.iterations?.toString()
            case 5: return item.priority?.toString()
            case 6: return item.addedBy?.toString()
            case 7: return item.configuredAt?.toString()
            default: throw new IllegalStateException("wrong column index: $column")
        }
    }
}
