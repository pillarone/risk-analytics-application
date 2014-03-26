package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.table.AbstractTableModel

abstract class SortableTableModel<T> extends AbstractTableModel {

    private List<T> backedList

    private List<IOrderChangedListener> listeners;

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
        int[] newIndices = fromItems.collect { backedList.indexOf(it) }.toArray(new int[fromItems.size()])
        def event = new SortedEvent<T>(sources, newIndices, fromItems.toArray() as T[])
        fireSortedEvent(event)
        return true
    }

    private List<IOrderChangedListener> fireSortedEvent(SortedEvent<T> event) {
        listeners.each { it.orderChanged(event) }
    }

    void setBackedList(List<T> backedList) {
        this.backedList = backedList
        fireTableDataChanged()
    }

    List<T> getBackedList() {
        return backedList
    }

    interface IOrderChangedListener {
        void orderChanged(SortedEvent event)
    }
}
