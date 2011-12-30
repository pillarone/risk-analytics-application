package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.dnd.TransferHandler
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.dnd.DnDTableData
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCListSelectionModel
import com.ulcjava.base.shared.UlcEventConstants
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableHelper
import com.ulcjava.base.application.dnd.DataFlavor
import com.ulcjava.base.application.dnd.Transferable
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.ULCList
import com.ulcjava.base.application.ULCScrollPane

import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCMenuItem
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableModel
import com.ulcjava.base.application.table.ULCTableColumn
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.OutputElementTable
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElementTableModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import com.ulcjava.base.application.event.IKeyListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.BorderFactory

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
    public CustomTablePane(CustomTable customTable) {
        super(customTable)

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
                    lastLeftClickedColumn = (ULCTableColumn) actionEvent.source
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
    private CustomTableView customTableView
    private CustomTableModel customTableModel

    private List<CopyCellData> copyData = new LinkedList<CopyCellData>()

    /**
     * Constructor
     *
     * @param customTableModel the CustomTableModel
     * @param pivotView the CustomTableView which contains the CustomTable
     */
    public CustomTable(CustomTableModel customTableModel, CustomTableView customTableView) {
        super(customTableModel)
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
        this.setAutoResizeMode(ULCTable.AUTO_RESIZE_OFF)
//        this.setDefaultRenderer(Object, new CustomTableCellRenderer())

        // listen if the user press the Enter-Key, and sets the focus to the cellEditTextField
        this.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                CustomTable.this.customTableView.cellEditTextField.requestFocus()
            }
        })

        // Add a listener the the column and the rows, so if another cell is selected, the CellEditTextField can be updated
        CellChangedListener cellChangedListener = new CellChangedListener()
        this.getSelectionModel().addListSelectionListener(cellChangedListener)
        this.getColumnModel().getSelectionModel().addListSelectionListener(cellChangedListener)

        // TODO: Listener funktioniert mit Ctrl nicht
        this.setEnabled(true)
        this.addKeyListener(new IKeyListener() {
            void keyTyped(KeyEvent keyEvent) {

                if (keyEvent.shiftDown) {

                    // copy
                    if (keyEvent.keyChar == KeyEvent.VK_C || keyEvent.keyChar == KeyEvent.VK_X) {
                        copyData.clear()

                        int min_row = CustomTable.this.getSelectionModel().getMinSelectionIndex()
                        int max_row = CustomTable.this.getSelectionModel().getMaxSelectionIndex()
                        int min_col = CustomTable.this.getColumnModel().getSelectionModel().getMinSelectionIndex()
                        int max_col = CustomTable.this.getColumnModel().getSelectionModel().getMaxSelectionIndex()

                        for (int row = min_row; row <= max_row; row++) {
                            for (int col = min_col; col <= max_col; col++) {
                                copyData.add(new CopyCellData(row, col, CustomTable.this.customTableModel.getDataAt(row, col)))

                                // cut
                                if (keyEvent.keyChar == KeyEvent.VK_X) {
                                    CustomTable.this.customTableModel.setValueAt("", row, col)
                                }
                            }
                        }

                    }

                    // paste
                    if (keyEvent.keyChar == KeyEvent.VK_V) {
                        int row = CustomTable.this.getSelectionModel().getMinSelectionIndex()
                        int col = CustomTable.this.getColumnModel().getSelectionModel().getMinSelectionIndex()

                        int last_origin_row = null
                        int last_origin_col = null

                        for (CopyCellData copyCellData: copyData) {
                            if (last_origin_row != null && last_origin_col != null) {
                                row += copyCellData.origin_row - last_origin_row
                                col += copyCellData.origin_col - last_origin_col
                            }

                            Object data = CustomTableHelper.copyData(copyCellData.data, row - copyCellData.origin_row, col - copyCellData.origin_col)
                            CustomTable.this.customTableModel.setValueAt(data, row, col)

                            last_origin_row = copyCellData.origin_row
                            last_origin_col = copyCellData.origin_col
                        }
                    }
                }
            }
        })
    }

    /**
     * Class which contains the data for a copy-operation
     */
    private class CopyCellData {
        public int origin_row
        public int origin_col
        public Object data

        public CopyCellData(int origin_row, int origin_col, Object data) {
            this.origin_row = origin_row
            this.origin_col = origin_col
            this.data = data
        }
    }

    /**
     * CellChangedListener for the CustomTable
     */
    private class CellChangedListener implements IListSelectionListener {
        void valueChanged(ListSelectionEvent listSelectionEvent) {
            if (CustomTable.this.customTableView.cellEditTextField.selectDataMode) {
                // If selectDataMode is on
                // get the selected Cells, and insert them in the CellEditTextField
                int rowIndexStart = CustomTable.this.getSelectedRow();
                int rowIndexEnd = CustomTable.this.getSelectionModel().getMaxSelectionIndex();
                int colIndexStart = CustomTable.this.getSelectedColumn();
                int colIndexEnd = CustomTable.this.getColumnModel().getSelectionModel().getMaxSelectionIndex();

                StringBuilder sb = new StringBuilder()

                if (colIndexStart != 0 || colIndexEnd != CustomTable.this.columnCount - 1) sb.append(CustomTableHelper.getColString(colIndexStart + 1))
                if (rowIndexStart != 0 || rowIndexEnd != CustomTable.this.rowCount - 1) sb.append((rowIndexStart + 1).toString())
                sb.append(":")
                if (colIndexStart != 0 || colIndexEnd != CustomTable.this.columnCount - 1) sb.append(CustomTableHelper.getColString(colIndexEnd + 1))
                if (rowIndexStart != 0 || rowIndexEnd != CustomTable.this.rowCount - 1) sb.append((rowIndexEnd + 1).toString())

                CustomTable.this.customTableView.cellEditTextField.insertData(sb.toString())

            } else {
                Object cellData = CustomTable.this.customTableModel.getDataAt(CustomTable.this.getSelectedRow(), CustomTable.this.getSelectedColumn())

                if (cellData instanceof OutputElement) {
                    CustomTable.this.customTableView.cellEditTextField.setVisible(false)
                    CustomTable.this.customTableView.dataCellEditPane.setVisible(true)
                    CustomTable.this.customTableView.dataCellEditPane.setData(CustomTable.this.getSelectedRow(), CustomTable.this.getSelectedColumn())

                } else {
                    // If the selectDataMode is off
                    // set the Value of the cell into the cellEditTextField
                    CustomTable.this.customTableView.cellEditTextField.setVisible(true)
                    CustomTable.this.customTableView.dataCellEditPane.setVisible(false)
                    CustomTable.this.customTableView.cellEditTextField.setText(CustomTable.this.getSelectedRow(), CustomTable.this.getSelectedColumn())
                }
            }
        }
    }

    /**
     * TransferHandler for DnD for the CustomTable, to import Data from the PreviewTree
     */
    private class MyTransferHandler extends TransferHandler {
        @Override
        public boolean importData(ULCComponent ulcComponent, Transferable transferable) {
            Object dragData = transferable.getTransferData(DataFlavor.DRAG_FLAVOR)
            Object dropData = transferable.getTransferData(DataFlavor.DROP_FLAVOR)

            if (dragData instanceof DnDTableData) {


                OutputElementTable table = ((DnDTableData) dragData).getTable()
                OutputElementTableModel tableModel = table.getModel()

                int rowToInsert = ((DnDTableData) dropData).getSelectedRows()[0]
                int colToInsert = ((DnDTableData) dropData).getSelectedColumns()[0]
                for (int row: table.getSelectedRows()) {
                    OutputElement outputElement = tableModel.getRowElement(row)

                    CustomTable.this.customTableModel.setValueAt(outputElement, rowToInsert++, colToInsert)

                    if (rowToInsert >= CustomTable.this.rowCount) {
                        rowToInsert = 0
                        colToInsert++

                        if (colToInsert >= CustomTable.this.columnCount) {
                            colToInsert = 0
                        }
                    }
                }
            }
        }

        @Override
        public void exportDone(ULCComponent ulcComponent, Transferable transferable, int i) {
        }
    }

    private class CustomTableCellRenderer extends DefaultTableCellRenderer {
        IRendererComponent getTableCellRendererComponent(ULCTable ulcTable, Object value, boolean isSelected, boolean hasFocus, int row) {

            if (CustomTable.this.customTableView.cellEditTextField.selectDataMode) {
                if (isSelected) {
                    this.setBorder(BorderFactory.createLineBorder(Color.red))

                } else if (hasFocus) {
                    this.setBorder(BorderFactory.createLineBorder(Color.blue))

                } else {
//                    this.setBorder(BorderFactory.createLineBorder(Color.green))
                    this.setBorder(BorderFactory.createEmptyBorder())
                }
            } else {
                if (isSelected) {
                    this.setBorder(BorderFactory.createLineBorder(Color.yellow))

                } else if (hasFocus) {
                    this.setBorder(BorderFactory.createLineBorder(Color.orange))

                } else {
//                    this.setBorder(BorderFactory.createLineBorder(Color.magenta))
                    this.setBorder(BorderFactory.createEmptyBorder())
                }
            }
            return this
        }
    }
}
