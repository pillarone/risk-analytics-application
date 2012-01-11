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
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.applicationframework.application.Action
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.ULCButton
import org.pillarone.riskanalytics.application.ui.main.action.ImportAllAction
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement

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
     * @return the RowHeader as a ULCList
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
                if (lastLeftClickedColumn != null) {
                    customTableModel.insertCol(lastLeftClickedColumn.getModelIndex())
                } else {
                    customTableModel.addCol()
                }
            }
        })
        colHeaderPopupMenu.add(insertColMenuItem)

        ULCMenuItem deleteColMenuItem = new ULCMenuItem("Delete Column")
        deleteColMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (lastLeftClickedColumn != null) {
                    customTableModel.deleteCol(lastLeftClickedColumn.getModelIndex())
                }
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

        // Copy/Paste Listener
        this.registerKeyboardAction(new CopyPasteActionListener(CopyPasteActionListener.Mode.COPY), KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        this.registerKeyboardAction(new CopyPasteActionListener(CopyPasteActionListener.Mode.CUT), KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        this.registerKeyboardAction(new CopyPasteActionListener(CopyPasteActionListener.Mode.PASTE), KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)

        this.registerKeyboardAction(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                for (int row : CustomTable.this.getSelectedRows()) {
                    for (int col : CustomTable.this.getSelectedColumns()) {
                        CustomTable.this.customTableModel.setValueAt("", row, col)
                    }
                }
            }
        }, KeyStroke.getKeyStroke (KeyEvent.VK_DELETE, 0), ULCComponent.WHEN_FOCUSED)


        ULCPopupMenu tablePopupMenu = new ULCPopupMenu()

        ULCMenuItem setTableSizeMenuItem = new ULCMenuItem("Set table size...")
        setTableSizeMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                TableSizeDialog dlg = new TableSizeDialog(CustomTable.this.customTableView.parent, CustomTable.this.rowCount, CustomTable.this.columnCount)
                dlg.visible = true

                dlg.addWindowListener(new IWindowListener() {
                    void windowClosing(WindowEvent windowEvent) {
                        if (windowEvent.source instanceof ULCButton && windowEvent.source.text == "OK") {
                            CustomTable.this.customTableModel.setNumberRows (dlg.getNumberRows())
                            CustomTable.this.customTableModel.setNumberCols (dlg.getNumberColumns())
                        }
                    }
                })

            }
        })
        tablePopupMenu.add(setTableSizeMenuItem)

        ULCMenuItem showFormulasMenuItem = new ULCMenuItem (customTableModel.editMode ? "show values" : "show formulas")
        showFormulasMenuItem.addActionListener(new IActionListener(){
            void actionPerformed(ActionEvent actionEvent) {
                customTableModel.editMode = !customTableModel.editMode
                customTableModel.fireTableDataChanged()
                showFormulasMenuItem.text = customTableModel.editMode ? "show values" : "show formulas"
            }
        })
        tablePopupMenu.add(showFormulasMenuItem)

        this.setComponentPopupMenu(tablePopupMenu)
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

                if (CustomTable.this.selectedRowCount == 1 && CustomTable.this.selectedColumnCount == 1) {
                    sb.append(CustomTableHelper.getColString(colIndexStart + 1))
                    sb.append((rowIndexStart + 1).toString())
                } else {
                    if (colIndexStart != 0 || colIndexEnd != CustomTable.this.columnCount - 1) sb.append(CustomTableHelper.getColString(colIndexStart + 1))
                    if (rowIndexStart != 0 || rowIndexEnd != CustomTable.this.rowCount - 1) sb.append((rowIndexStart + 1).toString())
                    sb.append(":")
                    if (colIndexStart != 0 || colIndexEnd != CustomTable.this.columnCount - 1) sb.append(CustomTableHelper.getColString(colIndexEnd + 1))
                    if (rowIndexStart != 0 || rowIndexEnd != CustomTable.this.rowCount - 1) sb.append((rowIndexEnd + 1).toString())
                }

                CustomTable.this.customTableView.cellEditTextField.insertData(sb.toString())

            } else {
                Object cellData = CustomTable.this.customTableModel.getDataAt(CustomTable.this.getSelectedRow(), CustomTable.this.getSelectedColumn())

                if (cellData instanceof OutputElement) {
                    CustomTable.this.customTableView.cellEditTextField.editable = false
                    CustomTable.this.customTableView.dataCellEditPane.setVisible(true)
                    CustomTable.this.customTableView.dataCellEditPane.setData(CustomTable.this.getSelectedRow(), CustomTable.this.getSelectedColumn())

                } else {
                    // If the selectDataMode is off
                    // set the Value of the cell into the cellEditTextField
                    CustomTable.this.customTableView.cellEditTextField.editable = true
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

            if (dragData instanceof DnDTableData && dropData instanceof DnDTableData) {

                OutputElementTable table = dragData.getTable()
                OutputElementTableModel tableModel = table.getModel()

                int dropRowOrigin = dropData.getSelectedRows()[0]
                int dropColOrigin = dropData.getSelectedColumns()[0]

                int dropRow = dropRowOrigin
                int dropCol = dropColOrigin

                for (int dragRow : table.getSelectedRows()) {
                    DataCellElement dataCellElement = new DataCellElement (tableModel.getRowElement(table.convertRowIndexToModel(dragRow)))

                    // TODO: add period, statistic to DataCellElement
                    dataCellElement.periodIndex = 0

                    dataCellElement.updateValue()
                    CustomTable.this.customTableModel.setValueAt(dataCellElement, dropRow++, dropCol)

                    if (dropRow >= CustomTable.this.rowCount) {
                        dropRow = 0
                        dropCol++

                        if (dropCol >= CustomTable.this.columnCount) {
                            dropCol = 0
                        }
                    }
                }

                CustomTable.this.selectionModel.setSelectionInterval(dropRowOrigin, dropRowOrigin)
                CustomTable.this.columnModel.selectionModel.setSelectionInterval(dropColOrigin, dropColOrigin)
            }
        }

        @Override
        public void exportDone(ULCComponent ulcComponent, Transferable transferable, int i) {
        }
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

    private List<CopyCellData> copyData = new LinkedList<CopyCellData>()
    private class CopyPasteActionListener implements IActionListener {
        public enum Mode {
            COPY,
            CUT,
            PASTE
        }

        private Mode mode

        public CopyPasteActionListener (Mode mode) {
            this.mode = mode
        }

        void actionPerformed(ActionEvent actionEvent) {
            switch (mode) {
                case Mode.COPY:
                case Mode.CUT:
                    CustomTable.this.copyData.clear()
                    int min_row = CustomTable.this.getSelectionModel().getMinSelectionIndex()
                    int max_row = CustomTable.this.getSelectionModel().getMaxSelectionIndex()
                    int min_col = CustomTable.this.getColumnModel().getSelectionModel().getMinSelectionIndex()
                    int max_col = CustomTable.this.getColumnModel().getSelectionModel().getMaxSelectionIndex()
                    for (int row = min_row; row <= max_row; row++) {
                        for (int col = min_col; col <= max_col; col++) {
                            CustomTable.this.copyData.add(new CopyCellData(row, col, CustomTable.this.customTableModel.getDataAt(row, col)))

                            // cut
                            if (mode == Mode.CUT) {
                                CustomTable.this.customTableModel.setValueAt("", row, col)
                            }
                        }
                    }
                    break
                
                case Mode.PASTE:

                    if (CustomTable.this.copyData.size() > 1) {
                        int row = CustomTable.this.getSelectionModel().getMinSelectionIndex()
                        int col = CustomTable.this.getColumnModel().getSelectionModel().getMinSelectionIndex()

                        int last_origin_row = null
                        int last_origin_col = null

                        for (CopyCellData copyCellData: CustomTable.this.copyData) {
                            if (last_origin_row != null && last_origin_col != null) {
                                row += copyCellData.origin_row - last_origin_row
                                col += copyCellData.origin_col - last_origin_col
                            }

                            Object data = CustomTableHelper.copyData(copyCellData.data, row - copyCellData.origin_row, col - copyCellData.origin_col)

                            if ((data instanceof String) == false) {
                                if (((DataCellElement)data).updateSpecificPathWithVariables(CustomTable.this.customTableModel))
                                    ((DataCellElement)data).updateValue()
                            }
                            CustomTable.this.customTableModel.setValueAt(data, row, col)

                            last_origin_row = copyCellData.origin_row
                            last_origin_col = copyCellData.origin_col
                        }
                        CustomTable.this.selectionModel.setSelectionInterval(row, row)
                        CustomTable.this.getColumnModel().selectionModel.setSelectionInterval(col, col)


                    } else if (CustomTable.this.copyData.size() > 0) {
                        int min_row = CustomTable.this.getSelectionModel().getMinSelectionIndex()
                        int max_row = CustomTable.this.getSelectionModel().getMaxSelectionIndex()
                        int min_col = CustomTable.this.getColumnModel().getSelectionModel().getMinSelectionIndex()
                        int max_col = CustomTable.this.getColumnModel().getSelectionModel().getMaxSelectionIndex()

                        CopyCellData copyCellData = CustomTable.this.copyData[0]

                        for (int row = min_row; row <= max_row; row++) {
                            for (int col = min_col; col <= max_col; col++) {
                                Object data = CustomTableHelper.copyData(copyCellData.data, row - copyCellData.origin_row, col - copyCellData.origin_col)
                                if ((data instanceof String) == false) {
                                    if (((DataCellElement)data).updateSpecificPathWithVariables(CustomTable.this.customTableModel))
                                        ((DataCellElement)data).updateValue()
                                }
                                CustomTable.this.customTableModel.setValueAt(data, row, col)
                            }
                        }

                        CustomTable.this.selectionModel.setSelectionInterval(min_row, max_row)
                        CustomTable.this.getColumnModel().selectionModel.setSelectionInterval(min_col, max_col)
                    }
                    break
            }
        }
    }

//    private class CustomTableCellRenderer extends DefaultTableCellRenderer {
//        private int col
//        public CustomTableCellRenderer(int col) {
//            super ()
//            this.col = col
//        }
//
//        IRendererComponent getTableCellRendererComponent(ULCTable ulcTable, Object value, boolean isSelected, boolean hasFocus, int row) {
//
//            if (CustomTable.this.customTableView.cellEditTextField.selectDataMode) {
//                if (isSelected) {
//                    this.setBorder(BorderFactory.createLineBorder(Color.red))
//
//                } else if (hasFocus) {
//                    this.setBorder(BorderFactory.createLineBorder(Color.blue))
//
//                } else {
////                    this.setBorder(BorderFactory.createLineBorder(Color.green))
//                    this.setBorder(BorderFactory.createEmptyBorder())
//                }
//            } else {
//                if (isSelected) {
//                    this.setBorder(BorderFactory.createLineBorder(Color.yellow))
//
//                } else if (hasFocus) {
//                    this.setBorder(BorderFactory.createLineBorder(Color.orange))
//
//                } else {
////                    this.setBorder(BorderFactory.createLineBorder(Color.magenta))
//                    this.setBorder(BorderFactory.createEmptyBorder())
//                }
//            }
//            return this
//        }
//    }
}
