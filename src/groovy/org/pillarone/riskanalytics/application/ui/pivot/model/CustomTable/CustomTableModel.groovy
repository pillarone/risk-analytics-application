package org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable

import com.ulcjava.base.application.table.AbstractTableModel

import com.ulcjava.base.application.DefaultListModel

class CustomTableModel extends AbstractTableModel {
    List<String> columnNames
    List<List<Object>> data

    DefaultListModel rowHeaderModel

    boolean editMode

    CustomTableModel(List<List<Object>> data, List<String> columnNames) {
        this.columnNames = columnNames
        this.data = data

        rowHeaderModel = new DefaultListModel(new Object[0])
    }

    int addRow (List<Object> rowData, String rowName) {
        while (rowData.size() < columnCount) {
            rowData.add ("")
        }
        this.data.add(rowData)

        if (rowName.isEmpty())
            rowName = rowCount
        rowHeaderModel.add (rowName)
        fireTableRowsInserted(rowCount-1, rowCount-1)
        return rowCount-1
    }

    int addCol (String colName) {
        this.columnNames.add(colName)

        for (List<Object> row : data) {
            while (row.size() < columnCount) {
                row.add ("")
            }
        }

        fireTableStructureChanged()
        return columnCount-1
    }

    public int getColumnCount() {
        return columnNames.size()
    }

    public int getRowCount() {
        return data.size()
    }

    public String getColumnName(int col) {
        return columnNames[col]
    }

    public Object getDataAt(int row, int col) {
        if (row >= rowCount || col >= columnCount)
            return null

        return data[row][col]
    }

    public Object getValueAt(int row, int col) {
        if (row >= rowCount || col >= columnCount)
            return null

        // If editMode, just return the original data
        if (editMode == true) {
            return data[row][col]
        }

        // else check if the data is a formula
        String cellData = data[row][col]

        // if data is not a formula, just return the data
        if (cellData.startsWith("=") == false) {
            return cellData
        }

        // else execute the formula as groovy code
        String formula = CustomTableHelper.replaceVariables (this, cellData.substring(1), row, col)
        formula = CustomTableHelper.executeFunctions (formula)
        GroovyShell shell = new GroovyShell()
        Object value = shell.evaluate("return " + formula)

        return value
    }

    public void setColumnName (int col, String name) {
        columnNames.set (col, name)
        fireTableStructureChanged()
    }


    public int getID (int row) {
        return data[row][2] as int
    }

    public Class getColumnClass(int c) {
        return String.class

        if (getValueAt(0, c) == null)
            return null

        return getValueAt(0, c).class
    }

    public boolean isCellEditable(int row, int col) {
        return editMode
    }

    public void setValueAt(Object value, int row, int col) {
        if (row >= rowCount || col >= columnCount)
            return

        data[row][col] = value
        fireTableCellUpdated(row, col)
    }
}
