package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.table.AbstractTableModel

abstract class SortableListModel<T> extends AbstractTableModel {

    private List<T> backedList

    @Override
    int getRowCount() {
        return backedList.size()
    }

    SortableListModel(List<T> objects) {
        backedList = objects
    }

    boolean moveFromTo(int[] sources, int target) {
        List<T> fromItems = sources.collect { backedList[it] }

        //replace fromItems with null as placeholder
        sources.each { backedList[it] = null }

        //add from items to the to index
        fromItems.reverseEach {
            backedList.add(target, it)
        }

        //removed placeholder items
        backedList.removeAll([null])

        //TODO fire some kind of sort event
        //TODO fire more specific
        fireTableDataChanged()
        return true
    }

    void setBackedList(List<T> backedList) {
        this.backedList = backedList
        fireTableDataChanged()
    }

    List<T> getBackedList() {
        return backedList
    }
}
