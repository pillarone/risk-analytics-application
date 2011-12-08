package org.pillarone.riskanalytics.application.ui.pivot.model.DataNavigator

import com.ulcjava.base.application.table.AbstractTableModel


class TreeStructureTableModel extends AbstractTableModel {
    String[] columnNames
    List<Object[]> data

    TreeStructureTableModel(List<Object[]> data, String[] columnNames) {
        this.columnNames = columnNames
        this.data = data
    }

    boolean moveToTop (int row) {
        if (row == 0)
            return false

        for (int r = row; r > 0; r--) {
            moveUp (r)
        }
        return true
    }

    boolean moveToBottom (int row) {
        if (row == this.data.size()-1)
            return false

        for (int r = row; r < this.data.size()-1; r++) {
            moveDown (r)
        }
        return true;
    }

    boolean moveUp (int row) {
        if (row < 1)
            return false

        Object temp = this.data.get (row)
        this.data.set (row, this.data.get (row-1))
        this.data.set (row-1, temp)
        fireTableRowsUpdated(row-1, row)
        return true
    }

    boolean moveDown (int row) {
        if (row > this.data.size()-2)
            return false

        Object temp = this.data.get (row)
        this.data.set (row, this.data.get (row+1))
        this.data.set (row+1, temp)

        fireTableRowsUpdated(row, row+1)
        return true
    }

    void addRow (Object[] rowData) {
        this.data.add(rowData)
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data.get(row)[col];
    }

    public int getID (int row) {
        return data.get(row)[2] as int;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        if (col == 0)
            return true

        return false
    }

    public void setValueAt(Object value, int row, int col) {
        data.get(row)[col] = value
    }
}
