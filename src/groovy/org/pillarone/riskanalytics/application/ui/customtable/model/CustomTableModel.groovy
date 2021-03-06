package org.pillarone.riskanalytics.application.ui.customtable.model

import com.ulcjava.base.application.table.AbstractTableModel
import com.ulcjava.base.application.AbstractListModel
import org.nfunk.jep.ParseException
import java.util.regex.Pattern
import org.pillarone.riskanalytics.application.ui.customtable.view.CellRendererInformation
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * TableModel for the CustomTable
 *
 * @author ivo.nussbaumer
 */
public class CustomTableModel extends AbstractTableModel {

    static Log LOG = LogFactory.getLog(CustomTableModel.class);

    List<List<Object>> data
    private RowHeaderListModel rowHeaderModel

//    private GroovyShell groovyShell
    private MathParser mathParser

    public boolean editMode = false

    Map<String, List<String>> references = new HashMap<String, List<String>>()

    // list for the rendering information
    private List<List<CellRendererInformation>> rendererInformation = new LinkedList<List<CellRendererInformation>>()




    /**
     * Constructor
     *
     * @param data Initial Table-Data
     */
    public CustomTableModel(List<List<Object>> data) {
        this.data = data

        rowHeaderModel = new RowHeaderListModel(this)

//        groovyShell = new GroovyShell()
        mathParser = new MathParser();
    }

    /**
     * set the number of rows
     * @param rows the number of row
     */
    public void setNumberRows (int rows) {
        int rowDiff = rows - rowCount

        if (rowDiff == 0)
            return

        // more rows ?
        if (rowDiff > 0) {
            for (int i = 0; i < rowDiff; i++) {
                this.data.add (new LinkedList<Object>(Collections.nCopies(this.columnCount, "")))
            }
            fireTableRowsInserted(rowCount-1 - rowDiff, rowCount-1)
            rowHeaderModel.fireIntervalAdded(this, rowCount-1 - rowDiff, rowCount-1)
            return
        }

        // less rows?
        if (rowDiff < 0) {
            for (int i = 0; i < -rowDiff; i++)
                this.data.remove(this.data.size()-1)
            fireTableRowsDeleted(rowCount-1, rowCount-1 - rowDiff)
            rowHeaderModel.fireIntervalRemoved(this, rowCount-1, rowCount-1 - rowDiff)
            return
        }
    }

    /**
    * set the number of cols
     * @param cols the number of cols
     */
    public void setNumberCols (int cols) {
        int colDiff = cols - columnCount

        if (colDiff == 0)
            return

        // more cols?
        if (colDiff > 0) {
            for (List<Object> rowData : data) {
                while (rowData.size() < cols)
                    rowData.add ("")
            }
            fireTableStructureChanged()
            return
        }

        // less cols?
        if (colDiff < 0) {
            for (List<Object> rowData : data) {
                while (rowData.size() > cols)
                    rowData.remove(rowData.size()-1)
            }
            fireTableStructureChanged()
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

        updateVariables (row, +1, true)

        while (rowData.size() < columnCount) {
            rowData.add ("")
        }

        this.data.add(row, rowData)

        rendererInformation.add(row, [])

        fireTableRowsInserted(row, row)
        rowHeaderModel.fireContentsChanged(this, row, rowCount-1)
    }


    /**
     * Inserts a Column into the table
     *
     * @param col Position to insert the new Column
     */
    public void insertCol (int col) {
        updateVariables (col, +1, false)

        if (col < 0)
            col = columnCount

        for (List<Object> rowData : data) {
            rowData.add (col, "")
        }
        for (List<CellRendererInformation> rowRenderInfoData : rendererInformation) {
            rowRenderInfoData.add (col, null)
        }
        fireTableStructureChanged()
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
     * Adds a Column to the end of the table
     * @return new Number of Columns
     */
    public int addCol () {
        insertCol (columnCount)
        return columnCount-1
    }

    /**
     * Deletes a Row
     * @param row Index of the Row to delete
     */
    public void deleteRow (int row) {
        if (rowCount <= 1 || row < 0 || row >= rowCount)
            return

        updateVariables (row, -1, true)

        data.remove(row)
        rendererInformation.remove(row)
        fireTableRowsDeleted(row, row)
        rowHeaderModel.fireContentsChanged(this, row, rowCount-1)
    }

    /**
     * Deletes a Column
     * @param col Index of the Column to delete
     */
    public void deleteCol (int col) {
        if (columnCount <= 1)
            return

        updateVariables (col, -1, false)

        for (List<Object> rowData : data) {
            rowData.remove(col)
        }
        for (List<CellRendererInformation> rowRenderInfoData : rendererInformation) {
            rowRenderInfoData.remove(col)
        }
        fireTableStructureChanged()
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


    public Object getDataAt(String variable) {
        return getDataAt (CustomTableHelper.getRow (variable), CustomTableHelper.getCol (variable))
    }

    /**
     * Returns the data (formula, data, or constant-value) of the cell
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the data of the cell
     */
    public Object getDataAt(int row, int col) {
        if (row >= rowCount || col >= columnCount || row < 0 || col < 0)
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

        try {
            int row = CustomTableHelper.getRow (variable.toUpperCase())
            int col = CustomTableHelper.getCol (variable.toUpperCase())
            return getValueAt(row, col)
        } catch (Exception e) {
            return "#ERROR"
        }
    }

    /**
     * Returns the value to display (resolved formula, value of a data-cell) in the table of a cell
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the value to display of the cell
     */
    public Object getValueAt(int row, int col) {
        if (row >= rowCount || col >= columnCount || row < 0 || col < 0)
            return ""

        Object cellData = data[row][col]

        if (cellData == null)
            return ""

        // If editMode, just return the original data
        if (editMode) {
            if (cellData instanceof DataCellElement)
                cellData = "#" + cellData.path
            return cellData
        }

        // if cellData is a formula, resolve the formula
        if (cellData instanceof String) {
            if (cellData.startsWith("=") && cellData.length() > 1) {
//

                String formula = CustomTableHelper.replaceVariables (this, cellData.substring(1), row, col)

                // error in variable replacing?
                if (formula.startsWith("#"))
                    return formula

                try {
                    mathParser.parseExpression(formula)
                    return mathParser.getValue()
                } catch (ParseException e) {
                    return "#" + e.getMessage()
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
     *
     * @param new_data
     * @param variable
     */
    public void setValueAt(Object new_data, String variable) {
        setValueAt (new_data, CustomTableHelper.getRow(variable), CustomTableHelper.getCol(variable))
    }
    /**
     * sets the data of a cell
     *
     * @param new_data the data to set
     * @param row the row of the cell
     * @param col the column of the cell
     */
    public void setValueAt(Object new_data, int row, int col) {
        if (row >= rowCount || col >= columnCount || row < 0 || col < 0)
            return

        // remove references on the changed cell
        Object old_data = getDataAt(row,col)
        if (old_data instanceof DataCellElement) {
            for (String categoryValue : old_data.categoryMap.values()) {
                if (categoryValue.startsWith("=")) {
                    removeReference(categoryValue.substring(1).replace('$', ''),
                                    CustomTableHelper.getVariable(row, col))
                }
            }
        }
        if (old_data instanceof String && ((String)data[row][col]).startsWith("=")) {
            for (String variable : CustomTableHelper.getVariables (this, old_data, row, col))
                removeReference(variable, CustomTableHelper.getVariable(row, col))
        }

        data[row][col] = new_data
        fireTableCellUpdated(row, col)

        // add references to the changed cell
        if (new_data instanceof DataCellElement) {
            for (String categoryValue : new_data.categoryMap.values()) {
                if (categoryValue.startsWith("=")) {
                    addReference(categoryValue.substring(1).replace('$', ''),
                                 CustomTableHelper.getVariable(row, col))
                }
            }
        }
        if (new_data instanceof String && new_data.startsWith("=")) {
            for (String variable : CustomTableHelper.getVariables (this, new_data, row, col))
                addReference(variable, CustomTableHelper.getVariable(row, col))
        }

        // update referencing cells
        updateCellReferences (row, col)
    }

    /**
     * Add a reference to the reference list
     * @param targetCell   the cell which is referenced to
     * @param variableCell the referencing cell
     */
    public boolean addReference (String targetCell, String variableCell) {
        if (targetCell == variableCell)
            return false

        targetCell = targetCell.replace('$', '').toUpperCase()
        variableCell = variableCell.replace('$', '')
        if (references[targetCell] == null)
            references[targetCell] = new LinkedList<String>()

        references[targetCell].add (variableCell)
        return true
    }

    /**
     * Removes a reference to the reference list
     * @param targetCell   the cell which is referenced to
     * @param variableCell the referencing cell
     */
    public void removeReference (String targetCell, String variableCell) {
        targetCell = targetCell.replace('$', '')
        variableCell = variableCell.replace('$', '')
        if (references[targetCell] == null)
            return

        references[targetCell].remove (variableCell)
    }

    /**
     * Updates all cell, which are referencing to the changed cell (row, col)
     *
     * @param row the row of the changed cell
     * @param col the col of the changed cell
     */
    public void updateCellReferences (int row, int col) {
        if (references[CustomTableHelper.getVariable(row, col)] != null) {
            for (String cell : references[CustomTableHelper.getVariable(row, col)]) {
                int r = CustomTableHelper.getRow (cell)
                int c = CustomTableHelper.getCol (cell)
                if (getDataAt(r, c) instanceof DataCellElement){
                    DataCellElement oe = (DataCellElement)getDataAt(r, c)
                    oe.update(this)
                }
                fireTableCellUpdated(r, c)
                updateCellReferences (r, c)
            }
        }
    }

    /**
     * after inserting/deleting a row/col update the variables in the cells, so they still refer to the same cell
     *
     * @param insertPos  position where the col/row was inserted/deleted
     * @param numInserts how many rows/cols were inserted/deleted (value < 0 -> deleted, value > 0 -> inserted)
     * @param row        true if a row was inserted/deleted, false if it was a column
     */
    private void updateVariables (int insertPos, int numInserts, boolean row) {
        // deep clone references map
        Map<String, List<String>> referencesClone = new HashMap<String, List<String>>()
        for (String key : references.keySet())
            referencesClone.put (key, references[key].clone())

        for (String referencedCell : referencesClone.keySet()) {

            if ( row && CustomTableHelper.getRow (referencedCell) >= insertPos ||
                !row && CustomTableHelper.getCol (referencedCell) >= insertPos) {

                for (String referencingCell : referencesClone[referencedCell]) {

                    String old_formula = getDataAt(referencingCell)
                    int old_row = CustomTableHelper.getRow(referencedCell)
                    int old_col = CustomTableHelper.getCol(referencedCell)

                    // catch exceptions, if referencing cell is in the references-map because of a range
                    try {
                        // get old variable
                        Pattern variable_dollar_pattern = Pattern.compile("[\$]?" + CustomTableHelper.getColString(old_col+1) + "[\$]?" + (old_row+1).toString())
                        String old_variable = variable_dollar_pattern.matcher(old_formula)[0]

                        // generate new variable
                        String new_variable = ""
                        if (row)
                            new_variable = old_variable.replace ((old_row+1).toString(), (old_row+1+numInserts).toString())
                        else
                            new_variable = old_variable.replace (CustomTableHelper.getColString(old_col+1), CustomTableHelper.getColString(old_col+1+numInserts))

                        // replace old variable with new variable in the formula
                        int pos = 0
                        if (numInserts > 0) {
                            pos = old_formula.lastIndexOf(old_variable)
                        } else {
                            pos = old_formula.indexOf(old_variable)
                        }
                        StringBuilder new_formula = new StringBuilder(old_formula)
                        new_formula.replace (pos, pos+old_variable.size(), new_variable)

                        // set the new formula in the cell
                        setValueAt (new_formula.toString(), referencingCell)
                    } catch (Exception e) {
                        int apa = 12;
                    }
                }
            }
        }
    }
    
    public CellRendererInformation getCellRendererInformation (int row, int col) {
        if (rendererInformation[row] == null)
            rendererInformation[row] = new LinkedList<String>()

        if (rendererInformation[row][col] == null)
            rendererInformation[row][col] = new CellRendererInformation()

        return rendererInformation[row][col]
    }

    public void adjustPrecision (int row, int col, int adjustment) {
        getCellRendererInformation(row, col).precision += adjustment
        fireTableCellUpdated(row, col)
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
