package org.pillarone.riskanalytics.application.ui.pivot.view

import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.dnd.TransferHandler
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.dnd.DnDTableTreeData
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.dnd.DnDTableData
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCListSelectionModel
import com.ulcjava.base.shared.UlcEventConstants
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CustomTableHelper
import com.ulcjava.base.application.dnd.DataFlavor
import com.ulcjava.base.application.dnd.Transferable
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.ULCList
import com.ulcjava.base.application.ULCScrollPane
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CustomTableRowHeaderRenderer
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCMenuItem
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CustomTableModel
import com.ulcjava.base.application.table.ULCTableColumn

/**
 * The ScrollPane which contains the CustomTable and the RowHeader for the CustomTable
 *
 * @author ivo.nussbaumer
 */
public class CustomTablePane extends ULCScrollPane {
    private ULCList rowHeader
    private CustomTable customTable

    /**
     * Constructor
     *
     * @param customTable the CustomTable
     */
    public CustomTablePane (CustomTable customTable) {
        super (customTable)

        this.customTable = customTable

        rowHeader = initRowHeader(this.customTable.getModel())
        initColHeader(this.customTable.getModel())

        this.setPreferredSize(new Dimension(300, 300))
        this.setRowHeaderView(rowHeader)
    }

    /**
     * Creates the RowHeader
     *
     * @param customTableModel the CustomTableModel
     * @return the RowHeader as a ULCLisst
     */
    private ULCList initRowHeader(CustomTableModel customTableModel) {
        ULCList rowHeader = new ULCList(customTableModel.rowHeaderModel)
        rowHeader.setFixedCellWidth(50)
        rowHeader.setFixedCellHeight(this.customTable.getRowHeight())
        rowHeader.setCellRenderer(new CustomTableRowHeaderRenderer(this.customTable))

        // Row Header Context Menu
        ULCPopupMenu rowHeaderPopupMenu = new ULCPopupMenu()

        ULCMenuItem insertRolMenuItem = new ULCMenuItem("Insert Row")
        insertRolMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                customTableModel.insertRow(rowHeader.getSelectedIndex())
            }
        })
        rowHeaderPopupMenu.add(insertRolMenuItem)

        ULCMenuItem deleteRowMenuItem = new ULCMenuItem("Delete Row")
        deleteRowMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                customTableModel.deleteRow(rowHeader.getSelectedIndex())
            }
        })
        rowHeaderPopupMenu.add(deleteRowMenuItem)
        rowHeader.setComponentPopupMenu(rowHeaderPopupMenu)

        return rowHeader
    }

    private ULCTableColumn lastLeftClickedColumn // used for the column-context-menu
    /**
     * Creates the context-menu for the Column-Header
     * @param customTableModel the CustomTableModel
     */
    private void initColHeader(CustomTableModel customTableModel) {
        ULCPopupMenu colHeaderPopupMenu = new ULCPopupMenu()

        ULCMenuItem insertColMenuItem = new ULCMenuItem("Insert Column")
        insertColMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                customTableModel.insertCol(lastLeftClickedColumn.getModelIndex())
            }
        })
        colHeaderPopupMenu.add(insertColMenuItem)

        ULCMenuItem deleteColMenuItem = new ULCMenuItem("Delete Column")
        deleteColMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                customTableModel.deleteCol(lastLeftClickedColumn.getModelIndex())
            }
        })
        colHeaderPopupMenu.add(deleteColMenuItem)

        customTable.getTableHeader().setComponentPopupMenu(colHeaderPopupMenu)

        customTable.getTableHeader().addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.modifiers == ActionEvent.META_MASK) {
                    lastLeftClickedColumn = (ULCTableColumn)actionEvent.source
                }
            }
        })
    }
}

/**
 * The CustomTable
 *
 * @author ivo.nussbaumer
 */
public class CustomTable extends ULCTable {
    private CustomTableView  customTableView
    private CustomTableModel customTableModel

    /**
     * Constructor
     *
     * @param customTableModel the CustomTableModel
     * @param pivotView the CustomTableView which contains the CustomTable
     */
    public CustomTable (CustomTableModel customTableModel, CustomTableView customTableView) {
        super (customTableModel)
        this.customTableModel = customTableModel
        this.customTableView = customTableView
        initTable()
    }

    /**
     * Initialize the Table
     */
    private void initTable() {
        this.setSelectionMode(ULCListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        this.setRowSelectionAllowed(false)
        this.setColumnSelectionAllowed(false)
        this.setCellSelectionEnabled(true)
        this.setPreferredScrollableViewportSize(new Dimension(300, 300))
        ClientContext.setModelUpdateMode(customTableModel, UlcEventConstants.SYNCHRONOUS_MODE);
        this.setTransferHandler(new MyTransferHandler())

        // listen if the user press the Enter-Key, and sets the focus to the cellEditTextField
        this.addActionListener(new IActionListener(){
            void actionPerformed(ActionEvent actionEvent) {
                CustomTable.this.customTableView.cellEditTextField.requestFocus()
            }
        })

        // Add a listener the the column and the rows, so if another cell is selected, the CellEditTextField can be updated
        CellChangedListener cellChangedListener = new CellChangedListener()
        this.getSelectionModel().addListSelectionListener(cellChangedListener)
        this.getColumnModel().getSelectionModel().addListSelectionListener(cellChangedListener)
    }

    /**
     * CellChangedListener for the CustomTable
     */
    public class CellChangedListener implements IListSelectionListener {
        void valueChanged(ListSelectionEvent listSelectionEvent) {

            if (CustomTable.this.customTableView.cellEditTextField.selectDataMode) {
                // If selectDataMode is on
                // get the selected Cells, and insert them in the CellEditTextField
                int rowIndexStart = CustomTable.this.getSelectedRow();
                int rowIndexEnd = CustomTable.this.getSelectionModel().getMaxSelectionIndex();
                int colIndexStart = CustomTable.this.getSelectedColumn();
                int colIndexEnd = CustomTable.this.getColumnModel().getSelectionModel().getMaxSelectionIndex();



                StringBuilder sb = new StringBuilder()
                /*for (int row = rowIndexStart; row <= rowIndexEnd; row++) {
                    for (int col = colIndexStart; col <= colIndexEnd; col++) {
                        if (CustomTable.this.isCellSelected(row, col)) {
                            sb.append (CustomTableHelper.getColString(col+1) + (row+1).toString() + ";")
                        }
                    }
                }
                sb.deleteCharAt(sb.length()-1)*/

                if (colIndexStart != 0 || colIndexEnd != CustomTable.this.columnCount-1) sb.append (CustomTableHelper.getColString(colIndexStart+1))
                if (rowIndexStart != 0 || rowIndexEnd != CustomTable.this.rowCount-1) sb.append ((rowIndexStart+1).toString())
                sb.append (":")
                if (colIndexStart != 0 || colIndexEnd != CustomTable.this.columnCount-1) sb.append (CustomTableHelper.getColString(colIndexEnd+1))
                if (rowIndexStart != 0 || rowIndexEnd != CustomTable.this.rowCount-1) sb.append ((rowIndexEnd+1).toString())

                CustomTable.this.customTableView.cellEditTextField.insertData (sb.toString())

            } else {
                // If the selectDataMode is off
                // set the Value of the cell into the cellEditTextField
                CustomTable.this.customTableView.cellEditTextField.setText (CustomTable.this.getSelectedRow(), CustomTable.this.getSelectedColumn())
            }
        }
    }


    /**
     * TransferHandler for DnD for the CustomTable, to import Data from the PreviewTree
     */
    public class MyTransferHandler extends TransferHandler {
        @Override
        public boolean importData(ULCComponent ulcComponent, Transferable transferable) {
            Object transferData = transferable.getTransferData(DataFlavor.DRAG_FLAVOR)

            if (transferData instanceof DnDTableTreeData) {
                TreePath[] treePaths = ((DnDTableTreeData)transferable.getTransferData(DataFlavor.DRAG_FLAVOR)).getTreePaths()

                for (TreePath treePath in treePaths) {
                    /*PreviewNode node = (PreviewNode)treePath.getLastPathComponent();

                    String colString = node.getValueAt(0)
                    int colIndex = pivotModel.customTableModel.columnHeaderData.get (colString)
                    if (colIndex == null) {
                        colIndex = pivotModel.customTableModel.addCol (colString, true)
                    }

                    String rowString = node.parent.getPathString()
                    int rowIndex = pivotModel.customTableModel.rowHeaderData.get(rowString)
                    if (rowIndex == null) {
                        rowIndex = pivotModel.customTableModel.addRow ([], rowString, true)
                    }

                    pivotModel.customTableModel.setValueAt(node.getValueAt(1), rowIndex, colIndex)   */
                }

            } else if (transferData instanceof DnDTableData) {

                ULCTable table = ((DnDTableData)transferable.getTransferData(DataFlavor.DRAG_FLAVOR)).getTable()
                if (customTableView.resultNavigator != null) {
                    return true;
                    // TODO: simRun property in ResultNavigator
//                    SimulationRun simRun = customTableView.resultNavigator.simRun
//
//                    for (int row : table.getSelectedRows()) {
//
//                        List<Object> data = new LinkedList<Object>()
//                        for (int col = 0; col < table.columnCount; col++)
//                            data.add (table.getValueAt(row, col))
//
//                        String path_field = data[0]
//
//                        int periodIndex = 0
//                        String pathName = path_field.split("__")[0]
//                        String collectorName = "AGGREGATED"
//                        String fieldName = path_field.split("__")[1]
//
//                        double mean = ResultAccessor.getMean (simRun, periodIndex, pathName, collectorName, fieldName)
//                        if (mean == Double.NaN) {
//                            collectorName = "AGGREGATED_DRILL_DOWN"
//                            mean = ResultAccessor.getMean (simRun, periodIndex, pathName, collectorName, fieldName)
//                        }
//
//                        if (pivotModel.customTableModel.getColumnCount() < 1) {
//                            pivotModel.customTableModel.addCol ("Mean")
//                        }
//
//                        pivotModel.customTableModel.addRow ([mean], "")
//                    }
                }
            }

            return true
        }

        @Override
        public void exportDone(ULCComponent ulcComponent, Transferable transferable, int i) {
        }
    }
}
