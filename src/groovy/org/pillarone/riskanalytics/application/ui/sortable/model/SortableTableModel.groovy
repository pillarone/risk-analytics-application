package org.pillarone.riskanalytics.application.ui.sortable.model

import com.ulcjava.base.application.table.AbstractTableModel
import groovy.transform.CompileStatic

@CompileStatic
abstract class SortableTableModel<T> extends AbstractTableModel {

    private List<T> backedList

    private List<IOrderChangedListener> listeners

    @Override
    int getRowCount() {
        return backedList.size()
    }

    SortableTableModel(List<T> objects) {
        backedList = objects
        listeners = []
    }

    void addOrderChangedListener(IOrderChangedListener listener) {
        listeners.add(listener)
    }

    void removedOrderChangedListener(IOrderChangedListener listener) {
        listeners.remove(listener)
    }

    boolean moveFromTo(int[] sources, int target) {
        List<T> fromItems = sources.collect { int source -> backedList[source] }

        //replace fromItems with null as placeholder
        sources.each { int source -> backedList[source] = null }

        //add from items to the to index
        fromItems.reverseEach { t ->
            backedList.add((target != -1) ? target : backedList.size(), (T) t)
        }

        //removed placeholder items
        backedList.removeAll([null])
        fireTableDataChanged()
        int[] newIndices = fromItems.collect { backedList.indexOf(it) }.toArray(new int[fromItems.size()]) as int[]
        def event = new SortedEvent<T>(sources, newIndices, fromItems.toArray() as T[])
        fireSortedEvent(event)
        return true
    }

    private List<IOrderChangedListener> fireSortedEvent(SortedEvent<T> event) {
        listeners.each { IOrderChangedListener listener -> listener.orderChanged(event) }
    }

    void setBackedList(List<T> backedList) {
        this.backedList = backedList
        fireTableDataChanged()
    }

    List<T> getBackedList() {
        return backedList
    }
}

interface IOrderChangedListener {
    void orderChanged(SortedEvent event)
}