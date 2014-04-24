package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.google.common.base.Preconditions
import com.ulcjava.base.application.table.AbstractTableModel
import groovy.beans.Bindable

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

abstract class AbstractTableRowModel<T> {

    private final AbstractTableModel tableModel
    private final Map<Integer, StringProperty> properties
    private T object

    int row

    AbstractTableRowModel(int row, AbstractTableModel tableModel, T object, int columnCount) {
        this.tableModel = tableModel
        this.row = row
        properties = (0..(columnCount - 1)).collectEntries {
            [it, new StringProperty()]
        }
        setObject(object)
        addChangeListener(tableModel)
    }

    AbstractTableModel getTableModel() {
        return tableModel
    }

    private Map<Integer, StringProperty> addChangeListener(AbstractTableModel tableModel) {
        properties.each { int column, StringProperty property ->
            property.addPropertyChangeListener(new TableUpdateListener(column))
        }
    }

    private class TableUpdateListener implements PropertyChangeListener {
        private final int column

        TableUpdateListener(int column) {
            this.column = column
        }

        @Override
        void propertyChange(PropertyChangeEvent evt) {
            getTableModel().fireTableCellUpdated(getRow(), column)
        }
    }

    String getValueAt(int column) {
        properties[column].value
    }

    T getObject() {
        return object
    }

    void setObject(T info) {
        this.object = Preconditions.checkNotNull(info)
        update()
    }

    void update() {
        properties.each { int column, StringProperty property ->
            property.value = getValueFactory(column).call(object)
        }
    }

    abstract Closure<String> getValueFactory(int index)

    private static class StringProperty {
        @Bindable
        String value
    }
}
