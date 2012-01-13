package org.pillarone.riskanalytics.application.ui.customtable.model

import com.ulcjava.base.application.table.AbstractTableModel
import com.ulcjava.base.application.AbstractListModel
import org.nfunk.jep.JEP
import org.nfunk.jep.ParseException

/**
 * TableModel for the CustomTable
 *
 * @author ivo.nussbaumer
 */
public class CustomTableModel extends AbstractTableModel {
    List<List<Object>> data
    private RowHeaderListModel rowHeaderModel

//    private GroovyShell groovyShell
    private JEP myParser

    public boolean editMode

    Map<String, List<String>> references = new HashMap<String, List<String>>()

    public void addReference (String targetCell, String variableCell) {
        if (references[targetCell] == null)
            references[targetCell] = new LinkedList<String>()

        references[targetCell].add (variableCell)
    }
    public void removeReference (String targetCell, String variableCell) {
        if (references[targetCell] == null)
            return

        references[targetCell].remove (variableCell)
    }

    /**
     * Constructor
     *
     * @param data Initial Table-Data
     */
    public CustomTableModel(List<List<Object>> data) {
        this.data = data

        rowHeaderModel = new RowHeaderListModel(this)

//        groovyShell = new GroovyShell()
        myParser = new JEP();
        myParser.addStandardFunctions()
        myParser.addFunction("mean", new Mean())
    }


    /**
     * Inserts a Column into the table
     *
     * @param col Position to insert the new Column
     */
    public void insertCol (int col) {
        for (List<Object> rowData : data) {
            rowData.add (col, "")
        }
        fireTableStructureChanged()
    }

    /**
     * Adds a Column to the end of the table
     * @return new Number of Columns
     */
    public int addCol () {
        insertCol (columnCount)
        return columnCount-1
    }

    /**
     * Deletes a Column
     * @param col Index of the Column to delete
     */
    public void deleteCol (int col) {
        if (columnCount <= 1)
            return

        for (List<Object> rowData : data) {
            rowData.remove(col)
        }
        fireTableStructureChanged()
    }

    public void setNumberCols (int cols) {
        int colDiff = cols - columnCount

        if (colDiff == 0)
            return

        if (colDiff > 0) {
            for (List<Object> rowData : data) {
                while (rowData.size() < cols)
                    rowData.add ("")
            }
            fireTableStructureChanged()
            return
        }
        if (colDiff < 0) {
            for (List<Object> rowData : data) {
                while (rowData.size() > cols)
                    rowData.remove(rowData.size()-1)
            }
            fireTableStructureChanged()
            return
        }
    }

    public void setNumberRows (int rows) {
        int rowDiff = rows - rowCount

        if (rowDiff == 0)
            return

        if (rowDiff > 0) {
            for (int i = 0; i < rowDiff; i++)
                this.data.add ([])
            fireTableRowsInserted(rowCount-1 - rowDiff, rowCount-1)
            rowHeaderModel.fireIntervalAdded(this, rowCount-1 - rowDiff, rowCount-1)
            return
        }

        if (rowDiff < 0) {
            for (int i = 0; i < -rowDiff; i++)
                this.data.remove(this.data.size()-1)
            fireTableRowsDeleted(rowCount-1, rowCount-1 - rowDiff)
            rowHeaderModel.fireIntervalRemoved(this, rowCount-1, rowCount-1 - rowDiff)
            return
        }
    }

    /**
     * Inserts a Row into the table
     *
     * @param col Position to insert the new Row
     */
    public void insertRow (int row, List<Object> rowData = null) {
        if (rowData == null)
            rowData = new LinkedList<Object>()

        if (row < 0)
            row = rowCount

        while (rowData.size() < columnCount) {
            rowData.add ("")
        }

        this.data.add(row, rowData)

        fireTableRowsInserted(row, row)
        rowHeaderModel.fireContentsChanged(this, row, rowCount-1)
    }

    /**
     * Adds a Row to the end of the table
     * @return new Number of Row
     */
    public int addRow (List<Object> rowData = null) {
        insertRow (rowCount, rowData)
        return rowCount-1
    }

    /**
     * Deletes a Row
     * @param row Index of the Row to delete
     */
    public void deleteRow (int row) {
        if (rowCount <= 1 || row < 0 || row >= rowCount)
            return

        data.remove(row)
        fireTableRowsDeleted(row, row)
        rowHeaderModel.fireContentsChanged(this, row, rowCount-1)
    }

    /**
     * Calculates the number of columns, depending on the stored data
     * @return
     */
    public int getColumnCount() {
        int colCount = 0
        for (List<Object> rowData : data) {
            colCount = (rowData.size() > colCount) ? rowData.size() : colCount
        }
        return colCount
    }

    /**
     * Calculates the number of rows, depending on the stored data
     * @return
     */
    public int getRowCount() {
        return data.size()
    }

    /**
     * Returns the Header-Name of the Column, formatted as an Excel-Like string
     * @param col the index of the column
     * @return header-name as an excel-like string
     */
    public String getColumnName(int col) {
        return CustomTableHelper.getColString(col+1)
    }

    public Class getColumnClass(int col) {
        return String.class
    }

    public boolean isCellEditable(int row, int col) {
        return false
    }

    /**
     * Returns the data (formula, data, or constant-value) of the cell
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the data of the cell
     */
    public Object getDataAt(int row, int col) {
        if (row >= rowCount || col >= columnCount)
            return null

        return data[row][col]
    }

    /**
     * Returns the value to display (resolved formula, value of a data-cell) in the table of a cell
     *
     * @param variable The variable, which is a excel-like String (e.g. B4)
     * @return the value to display of the cell
     */
    public Object getValueAt (String variable) {
        variable = variable.replace ('$', '')

        int row = CustomTableHelper.getRow (variable)
        int col = CustomTableHelper.getCol (variable)

        return getValueAt(row, col)
    }

    /**
     * Returns the value to display (resolved formula, value of a data-cell) in the table of a cell
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the value to display of the cell
     */
    public Object getValueAt(int row, int col) {
        if (row >= rowCount || col >= columnCount)
            return null

        Object cellData = data[row][col]

        // If editMode, just return the original data
        if (editMode == true) {
            if (cellData instanceof DataCellElement)
                cellData = "#" + cellData.path
            return cellData
        }

        // if cellData is a formula, resolve the formula
        if (cellData instanceof String) {
            if (cellData.startsWith("=")) {
                String formula = CustomTableHelper.replaceVariables (this, cellData.substring(1), row, col)

//                formula = CustomTableHelper.executeFunctions (formula)
//                try {
//                    Object value = groovyShell.evaluate("return " + formula)
//                    return value
//                } catch (Exception e) {
//                    return "#ERROR"
//                }

                try {
                    myParser.parseExpression(formula)

                    if (myParser.hasError())
                        return myParser.getErrorInfo()

                    double value = myParser.getValue()
                    return value
                } catch (ParseException e) {
                    return e.getMessage()
                }

            } else if (cellData.isNumber()) {
                double value = Double.parseDouble(cellData)
                return value
            }
            return cellData
        }

        // if cellData is a data-Reference, get the value from the database
        if (cellData instanceof DataCellElement) {
            return cellData.value
        }

        return cellData
    }

    /**
     * sets the data of a cell
     *
     * @param value the data to set
     * @param row the row of the cell
     * @param col the column of the cell
     */
    public void setValueAt(Object value, int row, int col) {
        if (row >= rowCount || col >= columnCount)
            return

        // remove references
        if (data[row][col] instanceof DataCellElement) {
            DataCellElement oe = data[row][col]
            for (String category : oe.categoryMap.keySet()) {
                if (oe.categoryMap[category].startsWith("=")) {
                    removeReference(oe.categoryMap[category].substring(1),
                                    CustomTableHelper.getVariable(row, col))
                }
            }
        }
        if (data[row][col] instanceof String && ((String)data[row][col]).startsWith("=")) {
            for (String variable : CustomTableHelper.getVariables (this, data[row][col], row, col))
                removeReference(variable, CustomTableHelper.getVariable(row, col))
        }

        data[row][col] = value
        fireTableCellUpdated(row, col)

        // add references
        if (value instanceof DataCellElement) {
            DataCellElement oe = value
            for (String category : oe.categoryMap.keySet()) {
                if (oe.categoryMap[category].startsWith("=")) {
                    addReference(oe.categoryMap[category].substring(1),
                                 CustomTableHelper.getVariable(row, col))
                }
            }
        }
        if (value instanceof String && ((String)value).startsWith("=")) {
            for (String variable : CustomTableHelper.getVariables (this, value, row, col))
                addReference(variable, CustomTableHelper.getVariable(row, col))
        }

        // update referencing cells
        updateCellReferences (row, col)
    }

    private void updateCellReferences (int row, int col) {
        if (references[CustomTableHelper.getVariable(row, col)] != null) {
            for (String cell : references[CustomTableHelper.getVariable(row, col)]) {
                int r = CustomTableHelper.getRow (cell)
                int c = CustomTableHelper.getCol (cell)
                if (getDataAt(r, c) instanceof DataCellElement){
                    DataCellElement oe = getDataAt(r, c)
                    if (oe.updateSpecificPathWithVariables(this))
                        oe.updateValue()
                }
                fireTableCellUpdated(r, c)
                updateCellReferences (r, c)
            }
        }
    }

    /**
     * ListModel for the RowHeader-List
     */
    private class RowHeaderListModel extends AbstractListModel {
        CustomTableModel customTableModel

        /**
         * Constructor
         * @param customTableModel the CustomTableModel
         */
        public RowHeaderListModel (CustomTableModel customTableModel) {
            this.customTableModel = customTableModel
        }

        /**
         * The size of the RowHeaderListModel is the number of rows in the CustomTableModel
         * @return
         */
        public int getSize() {
            return customTableModel.rowCount
        }

        /**
         * the Name of a row is just the number of the row+1
         * @param i the row index
         * @return the name of the element
         */
        public Object getElementAt(int row) {
            return (row+1).toString()
        }
    }
}
