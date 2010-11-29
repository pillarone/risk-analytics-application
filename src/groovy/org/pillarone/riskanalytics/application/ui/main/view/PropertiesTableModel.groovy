package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.table.AbstractTableModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class PropertiesTableModel extends AbstractTableModel {

    Object[][] array

    public PropertiesTableModel(Object[][] array) {
        super()
        this.@array = array
    }

    public int getRowCount() {
        array.length
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        array[rowIndex][columnIndex]
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        false
    }
}
