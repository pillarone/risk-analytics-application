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
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement
import com.ulcjava.base.application.dnd.DnDLabelData
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.shared.IWindowConstants
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import com.ulcjava.base.application.datatype.ULCNumberDataType
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import com.ulcjava.base.application.event.IKeyListener

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
    CustomTableModel customTableModel


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
        this.setDefaultRenderer(Object.class, new CustomTableCellRenderer())

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

         // Delete Listener
        this.registerKeyboardAction(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                for (int row : CustomTable.this.getSelectedRows()) {
                    for (int col : CustomTable.this.getSelectedColumns()) {
                        CustomTable.this.customTableModel.setValueAt("", row, col)
                    }
                }
            }
        }, KeyStroke.getKeyStroke (KeyEvent.VK_DELETE, 0), ULCComponent.WHEN_FOCUSED)

        // Other Keys Listener -> focus on cellEditTextField and start typing
        this.addKeyListener(new IKeyListener(){
            void keyTyped(KeyEvent keyEvent) {
                if (CustomTable.this.customTableModel.getDataAt (CustomTable.this.getSelectedRow(), CustomTable.this.getSelectedColumn()) instanceof DataCellElement == false) {
                    CustomTable.this.customTableView.cellEditTextField.requestFocus()
                    CustomTable.this.customTableView.cellEditTextField.text = keyEvent.keyChar
                }
            }
        })


        ULCPopupMenu tablePopupMenu = new ULCPopupMenu()

        ULCMenuItem setTableSizeMenuItem = new ULCMenuItem("Set table size...")
        setTableSizeMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                TableSizeDialog dlg = new TableSizeDialog(CustomTable.this.customTableView.parent, CustomTable.this.rowCount, CustomTable.this.columnCount)
                dlg.toFront()
                dlg.locationRelativeTo = UlcUtilities.getWindowAncestor(CustomTable.this.customTableView.parent)
                dlg.defaultCloseOperation = IWindowConstants.DISPOSE_ON_CLOSE
                dlg.visible = true

                dlg.addWindowListener(new IWindowListener() {
                    void windowClosing(WindowEvent windowEvent) {
                        if (dlg.isCancel == false) {
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
                    CustomTable.this.customTableView.cellEditTextField.setText(CustomTable.this.getSelectedRow(), CustomTable.this.getSelectedColumn())

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

            // insert data from the result navigator
            if (dragData instanceof DnDTableData && dropData instanceof DnDTableData) {
                OutputElementTable table = dragData.getTable()
                OutputElementTableModel tableModel = table.getModel()

                int dropRowOrigin = dropData.getSelectedRows()[0]
                int dropColOrigin = dropData.getSelectedColumns()[0]

                int dropRow = dropRowOrigin
                int dropCol = dropColOrigin

                for (int dragRow : table.getSelectedRows()) {
                    DataCellElement dataCellElement = new DataCellElement (tableModel.getRowElement(table.convertRowIndexToModel(dragRow)),
                                                                           table.keyfigureSelection.period,
                                                                           table.keyfigureSelection.keyfigure,
                                                                           table.keyfigureSelection.keyfigureParameter)

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

            // insert category values from the comboBox in the DataCellEditPane
            if (dragData instanceof DnDLabelData && dropData instanceof DnDTableData) {
                // get the comboBox with the values
                ULCComboBox combo = CustomTable.this.customTableView.dataCellEditPane.categoryComboBoxes[dragData.getLabel().getName()]

                // copy the values of the combo in a list
                List<String> categoryValues = new LinkedList<String>()
                for (int i = 0; i < combo.getItemCount(); i++) {
                    categoryValues.add(combo.getItemAt(i))
                }

                int dropRow = dropData.getSelectedRows()[0]
                int dropCol = dropData.getSelectedColumns()[0]

                // show category value insert dialog
                boolean vertical = true
                if (CustomTable.this.customTableModel.getDataAt(dropRow+1, dropCol) != null &&
                    CustomTable.this.customTableModel.getDataAt(dropRow, dropCol+1) == null) {
                    vertical = false
                }

                CategoryValuesInsertDialog dlg = new CategoryValuesInsertDialog(CustomTable.this.customTableView.parent,
                                                                                CustomTableHelper.getVariable (dropRow, dropCol),
                                                                                categoryValues,
                                                                                vertical)
                dlg.toFront()
                dlg.locationRelativeTo = UlcUtilities.getWindowAncestor(CustomTable.this.customTableView.parent)
                dlg.defaultCloseOperation = IWindowConstants.DISPOSE_ON_CLOSE
                dlg.visible = true

                // when dialog is closed, insert the values into the table
                dlg.addWindowListener(new IWindowListener() {
                    void windowClosing(WindowEvent windowEvent) {
                        if (dlg.isCancel == false) {
                            dropRow = CustomTableHelper.getRow (dlg.getStartCell())
                            dropCol = CustomTableHelper.getCol (dlg.getStartCell())
                            for (String categoryValue : categoryValues) {
                                CustomTable.this.customTableModel.setValueAt(categoryValue, dropRow, dropCol)
                                if (dlg.isVertical())
                                    dropRow++
                                else
                                    dropCol++
                            }
                        }
                    }
                })
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

    // internal clipboard
    private List<CopyCellData> copyData = new LinkedList<CopyCellData>()

    /**
     * Class which handles the copy/paste/cut operations
     */
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

                    StringBuilder excelCopyData = new StringBuilder()
                    for (int row = min_row; row <= max_row; row++) {
                        for (int col = min_col; col <= max_col; col++) {
                            // add data to the internal clipboard
                            Object data = CustomTable.this.customTableModel.getDataAt(row, col)
                            if (data == null)
                                data = ""
                            CustomTable.this.copyData.add(new CopyCellData(row, col, data))

                            // build the string, which will be added to the Windows clipboard
                            excelCopyData.append (CustomTable.this.customTableModel.getValueAt(row, col) )
                            excelCopyData.append ("\t")

                            // cut
                            if (mode == Mode.CUT) {
                                CustomTable.this.customTableModel.setValueAt("", row, col)
                            }
                        }
                        // remove last TAB and add newline
                        excelCopyData.deleteCharAt (excelCopyData.length()-1)
                        excelCopyData.append ("\r\n")
                    }
                    // add data to the Windows clipboard
                    StringSelection ss = new StringSelection(excelCopyData.toString())
                    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard()
                    clip.setContents(ss, ss)
                    break

                case Mode.PASTE:

                    // copy more than one value
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

                            // update the variables
                            Object data = CustomTableHelper.copyData(copyCellData.data, row - copyCellData.origin_row, col - copyCellData.origin_col)

                            if ((data instanceof String) == false) {
                                ((DataCellElement)data).update(CustomTable.this.customTableModel)
                            }
                            CustomTable.this.customTableModel.setValueAt(data, row, col)

                            last_origin_row = copyCellData.origin_row
                            last_origin_col = copyCellData.origin_col
                        }
                        CustomTable.this.selectionModel.setSelectionInterval(row, row)
                        CustomTable.this.getColumnModel().selectionModel.setSelectionInterval(col, col)

                    // just one cell to copy --> insert the cell, in the whole selection
                    } else if (CustomTable.this.copyData.size() == 1) {
                        int min_row = CustomTable.this.getSelectionModel().getMinSelectionIndex()
                        int max_row = CustomTable.this.getSelectionModel().getMaxSelectionIndex()
                        int min_col = CustomTable.this.getColumnModel().getSelectionModel().getMinSelectionIndex()
                        int max_col = CustomTable.this.getColumnModel().getSelectionModel().getMaxSelectionIndex()

                        CopyCellData copyCellData = CustomTable.this.copyData[0]

                        for (int row = min_row; row <= max_row; row++) {
                            for (int col = min_col; col <= max_col; col++) {
                                Object data = CustomTableHelper.copyData(copyCellData.data, row - copyCellData.origin_row, col - copyCellData.origin_col)
                                if ((data instanceof String) == false) {
                                    ((DataCellElement)data).update(CustomTable.this.customTableModel)
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

    private class CustomTableCellRenderer extends DefaultTableCellRenderer {
        IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row) {
            setFormat(value)

            if (value instanceof String && ((String)value).isNumber() || value instanceof Number) {
                this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT)
            } else {
                this.setHorizontalAlignment(DefaultTableCellRenderer.LEFT)
            }

            if (isSelected) {
                this.setBackground (Color.lightGray)
            } else {
                this.setBackground (Color.white)
            }

            this.setFont (this.getFont().deriveFont(Font.PLAIN))

            return this
        }


        ULCNumberDataType numberDataType

        public setFormat(def value) {
            setDataType null
        }
        public setFormat(Number value) {
            setDataType(getNumberDataType())
        }
        public ULCNumberDataType getNumberDataType() {
            if (numberDataType == null) {
                numberDataType = DataTypeFactory.numberDataType
                numberDataType.setGroupingUsed true
                numberDataType.setMinFractionDigits 2
                numberDataType.setMaxFractionDigits 2
            }
            return numberDataType
        }
    }
}
