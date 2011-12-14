package org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable

import com.ulcjava.base.application.table.AbstractTableModel

import com.ulcjava.base.application.DefaultListModel

class CustomTableModel extends AbstractTableModel {
    List<String> columnNames
    Map<String, Integer> columnHeaderData
    List<List<Object>> data

    DefaultListModel rowHeaderModel
    Map<String, Integer> rowHeaderData

    boolean editMode

    CustomTableModel(List<List<Object>> data, List<String> columnNames) {
        this.columnNames = columnNames
        this.data = data

        columnHeaderData = new HashMap<String, Integer>()
        rowHeaderData = new HashMap<String, Integer>()

        rowHeaderModel = new DefaultListModel(new Object[0])
    }

    int addRow (List<Object> rowData, String rowName, boolean isDataRow = false) {
        while (rowData.size() < columnCount) {
            rowData.add ("")
        }
        this.data.add(rowData)

        if (rowName.isEmpty())
            rowName = rowCount

        rowHeaderModel.add (rowName)

        if (isDataRow) {
            rowHeaderData.put (rowName, rowCount-1)
        }

        fireTableRowsInserted(rowCount-1, rowCount-1)
        return rowCount-1
    }

    int addCol (String colName, boolean isDataCol = false) {
        if (colName.isEmpty())
            colName = CustomTableHelper.getColString(columnCount+1)

        this.columnNames.add(colName)

        for (List<Object> row : data) {
            while (row.size() < columnCount) {
                row.add ("")
            }
        }

        if (isDataCol) {
            columnHeaderData.put (colName, columnCount-1)
        }

        fireTableStructureChanged()
        return columnCount-1
    }
    void deleteCol (int col) {
        if (columnHeaderData.containsValue(col)) {
            for (String key : columnHeaderData.keySet()) {
                if (columnHeaderData[key] == col) {
                    columnHeaderData.remove(key)
                    break
                }
            }
            // update the bigger columns
        }
        for (List<Object> l : data) {
            l.remove(col)
        }
        columnNames.remove(col)
        fireTableStructureChanged()
    }

    void deleteRow (int row) {
        if (rowHeaderData.containsValue(row)) {
            for (String key : rowHeaderData.keySet()) {
                if (rowHeaderData[key] == row) {
                    rowHeaderData.remove(key)
                    break
                }
            }
            // update the bigger rows
        }
        data.remove(row)
        rowHeaderModel.remove(row)
        fireTableStructureChanged()
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
