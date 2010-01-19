package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.table.AbstractTableModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.application.ui.base.model.IBulkChangeable
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.MultiDimensionalParameterDimension

class MultiDimensionalParameterTableModel extends AbstractTableModel implements IBulkChangeable {
    private boolean bulkChange = false
    private List changedCells

    private AbstractMultiDimensionalParameter multiDimensionalParam
    private List listeners

    public MultiDimensionalParameterTableModel(AbstractMultiDimensionalParameter multiDimensionalParam) {
        this.@multiDimensionalParam = multiDimensionalParam
        this.listeners = []
    }

    void addListener(IModelChangedListener listener) {
        listeners << listener
    }

    void notifyModelChanged() {
        if (!bulkChange) {
            listeners.each {it.modelChanged()}
        }
    }

    public int getColumnCount() {
        return multiDimensionalParam.getColumnCount()
    }

    public int getRowCount() {
        return multiDimensionalParam.getRowCount()
    }

    public int getValueColumnCount() {
        return multiDimensionalParam.getValueColumnCount()
    }

    public int getValueRowCount() {
        return multiDimensionalParam.getValueRowCount()
    }

    public Object getValueAt(int row, int column) {
        Object value = multiDimensionalParam.getValueAt(row, column)
        if (value instanceof DateTime) {
            value = value.toDate()
        }
        return value
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (value == null) {
            return
        }
        if (value instanceof Date) {
            value = new DateTime(value.time)
        }

        Object oldValue = getValueAt(rowIndex, columnIndex)
        // This check is because the use of a ErrorManager in the Editor causes the wrong input to be send to the ULC-side.
        // The wrong input will be sent as String instead of a Number, so we check the type of the old value against the new one
        if (value instanceof String) {
            Class oldValueClazz = oldValue?.class
            if (oldValueClazz != String) {
                return
            }
        }

        if (value != null && !value.equals(oldValue)) {
            multiDimensionalParam.setValueAt value, rowIndex, columnIndex
            fireTableCellUpdated rowIndex, columnIndex
            if (multiDimensionalParam instanceof ComboBoxMatrixMultiDimensionalParameter) {
                //CBMMDP row/column titles are symmetric
                fireTableCellUpdated columnIndex, rowIndex
            }
            notifyModelChanged()
        }

    }

    public void fireTableCellUpdated(int row, int column) {
        if (bulkChange) {
            changedCells << new TableCellLocation(row: row, col: column)
        } else {
            super.fireTableCellUpdated(row, column)
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return multiDimensionalParam.isCellEditable(rowIndex, columnIndex)
    }

    List currentValues() {
        return multiDimensionalParam.getValues()
    }


    void setDimension(MultiDimensionalParameterDimension dimension) {
        multiDimensionalParam.setDimension(dimension)
        notifyModelChanged()
        fireTableStructureChanged()
    }

    boolean isValuesConverted() {
        return multiDimensionalParam.valuesConverted
    }

    public getPossibleValues(int row, int col) {
        multiDimensionalParam.getPossibleValues(row, col)
    }

    public void startBulkChange() {
        changedCells = []
        bulkChange = true
    }

    public void stopBulkChange() {
        bulkChange = false
        if (changedCells.size() > 0) {
            int minRow = changedCells.row.sort()[0]
            int maxRow = changedCells.row.sort()[-1]
            fireTableRowsUpdated minRow, maxRow
            notifyModelChanged()
        }

    }

    public boolean columnCountChangeable() {
        return multiDimensionalParam.columnCountChangeable()
    }

    public boolean rowCountChangeable() {
        return multiDimensionalParam.rowCountChangeable()
    }
}

class TableCellLocation {
    int row
    int col
}