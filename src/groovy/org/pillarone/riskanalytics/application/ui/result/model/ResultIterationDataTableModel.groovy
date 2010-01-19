package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.event.ITableModelListener
import com.ulcjava.base.application.event.TableModelEvent
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.table.ITableModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.ui.result.view.ResultIterationDataTableCellRenderer

class ResultIterationDataTableModel implements ITableModel {
    List<String> columnHeaders
    List tableValues
    List listeners = []
    ULCNumberDataType numberDataType

    public ResultIterationDataTableModel() {
        tableValues = [][]
        columnHeaders = []
    }

    public ResultIterationDataTableModel(List<List<Number>> tableValues, List<String> columnHeaders) {
        this.tableValues = tableValues
        this.columnHeaders = columnHeaders
    }




    public int getRowCount() {
        return tableValues.size()
    }

    public int getColumnCount() {
        if (tableValues[0]) {
            return tableValues[0].size()
        } else {
            return 0
        }
    }

    public String getColumnName(int column) {
        columnHeaders[column]
    }

    public Class getColumnClass(int column) {
        if (column == 0) {
            return Integer
        } else {
            return Number
        }
    }

    public Object getValueAt(int row, int column) {
        tableValues[row][column]
    }

    public void setValueAt(Object value, int row, int column) {
        tableValues[row][column] = value
    }

    public void addTableModelListener(ITableModelListener listener) {
        listeners << listener
    }

    public void removeTableModelListener(ITableModelListener listener) {
        listeners.remove(listener)
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public DefaultTableCellRenderer getCellRenderer(int columnIndex) {
        new ResultIterationDataTableCellRenderer(columnIndex)
    }

    protected fireModelChanged() {
        TableModelEvent event = new TableModelEvent(this)
        listeners.each {ITableModelListener listener -> listener.tableChanged(event)}
    }

    private ULCNumberDataType getNumberDataType() {
        if (!numberDataType) {
            numberDataType = LocaleResources.numberDataType
            numberDataType.setGroupingUsed true
            numberDataType.setMinFractionDigits 2
            numberDataType.setMaxFractionDigits 2
        }
        return numberDataType
    }

}