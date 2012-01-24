package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCListSelectionModel
import com.ulcjava.base.shared.UlcEventConstants
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableHelper
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.ULCList
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCMenuItem
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableModel
import com.ulcjava.base.application.table.ULCTableColumn
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.shared.IWindowConstants
import com.ulcjava.base.application.event.IKeyListener
import org.pillarone.riskanalytics.application.ui.customtable.view.CustomTableCopyPasteListener.CopyCellData

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

        rowHeader = initRowHeader((CustomTableModel)this.customTable.getModel())
        initColHeader((CustomTableModel)this.customTable.getModel())

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

        customTable.getTableHeader().setReorderingAllowed(false)
    }
}

/**
 * The CustomTable
 *
 * @author ivo.nussbaumer
 */
public class CustomTable extends ULCTable {
    CustomTableView customTableView
    CustomTableModel customTableModel


    // internal clipboard
    List<CopyCellData> copyData = new LinkedList<CopyCellData>()


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
        initContextMenu()
        iniKeyActions()
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
        this.setTransferHandler(new CustomTableDropHandler())
        this.setAutoResizeMode(ULCTable.AUTO_RESIZE_OFF)
        this.setDefaultRenderer(Object.class, new CustomTableCellRenderer())


        // Add a listener the the column and the rows, so if another cell is selected, the CellEditTextField can be updated
        CustomTableCellChangedListener cellChangedListener = new CustomTableCellChangedListener(this)
        this.getSelectionModel().addListSelectionListener(cellChangedListener)
        this.getColumnModel().getSelectionModel().addListSelectionListener(cellChangedListener)
    }

    /**
     * Initialize the key actions on the custom table
     */
    private void iniKeyActions () {
        // listen if the user press the Enter-Key, and sets the focus to the cellEditTextField
        this.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                CustomTable.this.customTableView.cellEditTextField.requestFocus()
            }
        })


        // Copy/Paste Listener
        this.registerKeyboardAction(new CustomTableCopyPasteListener(CustomTableCopyPasteListener.Mode.COPY), KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        this.registerKeyboardAction(new CustomTableCopyPasteListener(CustomTableCopyPasteListener.Mode.CUT), KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        this.registerKeyboardAction(new CustomTableCopyPasteListener(CustomTableCopyPasteListener.Mode.PASTE), KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)

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
                if (keyEvent.keyChar < 32 || keyEvent.keyChar == KeyEvent.VK_DELETE)
                    return

                if (CustomTable.this.customTableModel.getDataAt (CustomTable.this.getSelectedRow(), CustomTable.this.getSelectedColumn()) instanceof DataCellElement == false) {
                    CustomTable.this.customTableView.cellEditTextField.requestFocus()
                    CustomTable.this.customTableView.cellEditTextField.text = keyEvent.keyChar
                }
            }
        })
    }

    /**
     * initialize the context menu
     */
    private void initContextMenu () {
        ULCPopupMenu tableContextMenu = new ULCPopupMenu()
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
                        if (!dlg.isCancel) {
                            CustomTable.this.customTableModel.setNumberRows (dlg.getNumberRows())
                            CustomTable.this.customTableModel.setNumberCols (dlg.getNumberColumns())
                        }
                    }
                })

            }
        })
        tableContextMenu.add(setTableSizeMenuItem)

        ULCMenuItem showFormulasMenuItem = new ULCMenuItem (customTableModel.editMode ? "show values" : "show formulas")
        showFormulasMenuItem.addActionListener(new IActionListener(){
            void actionPerformed(ActionEvent actionEvent) {
                customTableModel.editMode = !customTableModel.editMode
                customTableModel.fireTableDataChanged()
                showFormulasMenuItem.text = customTableModel.editMode ? "show values" : "show formulas"
            }
        })
        tableContextMenu.add(showFormulasMenuItem)

        this.setComponentPopupMenu(tableContextMenu)
    }
}
