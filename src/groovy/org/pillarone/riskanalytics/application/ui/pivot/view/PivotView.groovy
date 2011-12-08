package org.pillarone.riskanalytics.application.ui.pivot.view

import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.ui.pivot.model.PivotModel
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.shared.UlcEventConstants
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.table.ULCTableColumn
import com.ulcjava.base.application.dnd.TransferHandler
import com.ulcjava.base.application.dnd.Transferable
import com.ulcjava.base.application.dnd.DataFlavor
import com.ulcjava.base.application.dnd.DnDTableTreeData
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCListSelectionModel
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCDialog
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.ULCList
import org.pillarone.riskanalytics.application.ui.pivot.model.DataNavigator.TreeStructureTableModel
import org.pillarone.riskanalytics.application.ui.pivot.model.DataNavigator.PreviewNode
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CustomTableRowHeaderRenderer
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CellEditDialog
import com.ulcjava.base.application.dnd.DnDData
import com.ulcjava.base.application.dnd.DnDTableData
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CustomTableHelper


class PivotView {
    ULCFrame parent
    ULCBoxPane content
    PivotModel pivotModel
    ULCTable dimensionTable
    ULCTable coordinateTable

    ULCTableTree previewTableTree

    ULCTable customTable

    ULCList rowHeader

    PivotView(PivotModel pivotModel) {
        this.pivotModel = pivotModel
        initComponents()
    }

    protected void initComponents() {
        content = new ULCBoxPane(false)
        content.setPreferredSize(new Dimension (400,400))

        ULCBoxPane col1 = new ULCBoxPane (true)

        col1.add (ULCBoxPane.BOX_EXPAND_TOP, createTreeStructure())
        col1.add (ULCBoxPane.BOX_EXPAND_EXPAND, createPreviewTree())

        content.add (ULCBoxPane.BOX_LEFT_EXPAND, col1)

        ULCBoxPane col2 = new ULCBoxPane (true)
        col2.add (ULCBoxPane.BOX_EXPAND_EXPAND, createCustomTable())
        
        content.add (ULCBoxPane.BOX_EXPAND_EXPAND, col2)
    }

    /**
     * creates the Dimensions- and Coordinates-Table and the corresponding buttons
     */
    public ULCBoxPane createTreeStructure() {
        // TreeStructure
        ULCBoxPane treeStructurePane = new ULCBoxPane(4, 1)

        // Dimensions Table
        dimensionTable = new ULCTable(pivotModel.dimensionTableModel)
        dimensionTable.getSelectionModel().addListSelectionListener(new IListSelectionListener() {
            void valueChanged(ListSelectionEvent listSelectionEvent) {
                int dimension_id = pivotModel.dimensionTableModel.getID(dimensionTable.selectedRow);
                coordinateTable.setModel(pivotModel.coordinateTableModels[dimension_id])
                coordinateTable.getColumnModel().getColumn(0).setPreferredWidth(20)
                coordinateTable.getColumnModel().getColumn(1).setPreferredWidth(100)
            }
        })
        dimensionTable.getColumnModel().getColumn(0).setPreferredWidth(20)
        dimensionTable.getColumnModel().getColumn(1).setPreferredWidth(100)
        dimensionTable.setSelectionMode(ULCListSelectionModel.SINGLE_SELECTION)
        dimensionTable.setRowSelectionAllowed(true)
        dimensionTable.setPreferredScrollableViewportSize(new Dimension(120, 100))
        dimensionTable.setShowVerticalLines(false)
        dimensionTable.setTableHeader (null)

        ULCScrollPane dimensionTableScrollPane = new ULCScrollPane(dimensionTable)
        dimensionTableScrollPane.setPreferredSize(new Dimension(140, 100))
        treeStructurePane.add (ULCBoxPane.BOX_EXPAND_EXPAND, dimensionTableScrollPane)

        treeStructurePane.add (ULCBoxPane.BOX_LEFT_EXPAND, createMoveButtons(dimensionTable))

        // Coordinates Table
        int dimension_id = pivotModel.dimensionTableModel.getID(0);
        coordinateTable = new ULCTable(pivotModel.coordinateTableModels[dimension_id])
        coordinateTable.getColumnModel().getColumn(0).setPreferredWidth(20)
        coordinateTable.getColumnModel().getColumn(1).setPreferredWidth(100)
        coordinateTable.setSelectionMode(ULCListSelectionModel.SINGLE_SELECTION)
        coordinateTable.setRowSelectionAllowed(true)
        coordinateTable.setPreferredScrollableViewportSize(new Dimension(120, 100))
        coordinateTable.setShowVerticalLines(false)
        coordinateTable.setTableHeader (null)

        ULCScrollPane coordinateTableScrollPane = new ULCScrollPane(coordinateTable)
        coordinateTableScrollPane.setPreferredSize(new Dimension(140, 100))
        treeStructurePane.add (ULCBoxPane.BOX_EXPAND_EXPAND, coordinateTableScrollPane)

        treeStructurePane.add (ULCBoxPane.BOX_LEFT_EXPAND, createMoveButtons(coordinateTable))

        return treeStructurePane
    }

    /**
     * creates a moveToTop, moveUp, moveDown, moveToBottom button for a table
     * @param table
     * @return
     */
    public ULCBoxPane createMoveButtons (ULCTable table) {
        ULCBoxPane pane = new ULCBoxPane(1, 4)

        UIUtils.ICON_DIRECTORY
        ULCButton dimensionTopButton = new ULCButton(UIUtils.getIcon ("pivot_top.png"))
        dimensionTopButton.setPreferredSize(new Dimension (16,16))
        dimensionTopButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (table.selectedRow < 0) return
                if ((table.getModel() as TreeStructureTableModel).moveToTop(table.selectedRow)) {
                    table.getSelectionModel().setSelectionInterval(0, 0)

                    table.scrollCellToVisible(dimensionTable.selectedRow, 0)
                }
            }
        })
        pane.add (0, 0, ULCBoxPane.BOX_CENTER_TOP, dimensionTopButton)

        ULCButton dimensionUpButton = new ULCButton(UIUtils.getIcon ("pivot_up.png"))
        dimensionUpButton.setPreferredSize(new Dimension (16,16))
        dimensionUpButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (table.selectedRow < 0) return
                if ((table.getModel() as TreeStructureTableModel).moveUp(table.selectedRow)) {
                    table.getSelectionModel().setSelectionInterval(table.selectedRow-1, table.selectedRow-1)

                    table.scrollCellToVisible(table.selectedRow, 0)
                }
            }
        })
        pane.add (0, 1, ULCBoxPane.BOX_CENTER_TOP, dimensionUpButton)

        ULCButton dimensionDownButton = new ULCButton(UIUtils.getIcon ("pivot_down.png"))
        dimensionDownButton.setPreferredSize(new Dimension (16,16))
        dimensionDownButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (table.selectedRow < 0) return
                if ((table.getModel() as TreeStructureTableModel).moveDown(table.selectedRow)) {
                    table.getSelectionModel().setSelectionInterval(table.selectedRow+1, table.selectedRow+1)

                    table.scrollCellToVisible(table.selectedRow, 0)
                }
            }
        })
        pane.add (0, 1, ULCBoxPane.BOX_CENTER_BOTTOM, dimensionDownButton)

        ULCButton dimensionBottomButton = new ULCButton(UIUtils.getIcon ("pivot_bottom.png"))
        dimensionBottomButton.setPreferredSize(new Dimension (16,16))
        dimensionBottomButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (table.selectedRow < 0) return
                if ((table.getModel() as TreeStructureTableModel).moveToBottom(table.selectedRow)) {
                    table.getSelectionModel().setSelectionInterval(table.rowCount-1, table.rowCount-1)

                    table.scrollCellToVisible(table.selectedRow, 0)
                }
            }
        })
        pane.add (0, 0, ULCBoxPane.BOX_CENTER_BOTTOM, dimensionBottomButton)

        return pane
    }

    /**
     * creates the PreviewTree and the Refresh button
     * @return
     */
    public ULCBoxPane createPreviewTree () {
        ULCBoxPane pane = new ULCBoxPane (true)

        // Preview Table Tree
        previewTableTree = new ULCTableTree(pivotModel.previewTableTreeModel);
        previewTableTree.expandAll()
        previewTableTree.setRootVisible(false);
        previewTableTree.setAutoResizeMode(ULCTableTree.AUTO_RESIZE_ALL_COLUMNS);

        previewTableTree.setDragEnabled (true)

        ULCScrollPane previewTreeScrollPane = new ULCScrollPane(previewTableTree)
        previewTreeScrollPane.setPreferredSize(new Dimension(320, 200))

        pane.add (ULCBoxPane.BOX_LEFT_EXPAND, previewTreeScrollPane)

        // Refresh button
        ULCButton refreshButton = new ULCButton("Refresh Preview")
        refreshButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                pivotModel.updatePreviewTree()
                previewTableTree.expandAll()
            }
        })
        pane.add (ULCBoxPane.BOX_CENTER_CENTER, refreshButton)

        return pane
    }

    /**
     * Creates the Custom Table and the corresponding Buttons
     */
    CellEditDialog cellEditDialog
    ULCTableColumn lastLeftClickedColumn
    public ULCBoxPane createCustomTable() {
        ULCBoxPane pane = new ULCBoxPane(true)

        // Custom Table
        customTable = new ULCTable(pivotModel.customTableModel)
        customTable.setSelectionMode(ULCListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        customTable.setRowSelectionAllowed(false)
        customTable.setColumnSelectionAllowed(false)
        customTable.setCellSelectionEnabled(true)
        customTable.setPreferredScrollableViewportSize(new Dimension(300, 300))
        ClientContext.setModelUpdateMode(pivotModel.customTableModel, UlcEventConstants.SYNCHRONOUS_MODE);
        customTable.setTransferHandler(new MyTransferHandler())

        customTable.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.actionCommand == "mouseDoubleClick") {
                    cellEditDialog = new CellEditDialog(parent, pivotModel.customTableModel, customTable.getSelectedRow(), customTable.getSelectedColumn())
                    cellEditDialog.setVisible(true)
                }
            }
        })

        customTable.getSelectionModel().addListSelectionListener(new IListSelectionListener() {
            void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (cellEditDialog != null && cellEditDialog.isVisible() && cellEditDialog.selectDataMode) {
                    int rowIndexStart = customTable.getSelectedRow();
                    int rowIndexEnd = customTable.getSelectionModel().getMaxSelectionIndex();
                    int colIndexStart = customTable.getSelectedColumn();
                    int colIndexEnd = customTable.getColumnModel().getSelectionModel().getMaxSelectionIndex();

                    StringBuilder sb = new StringBuilder()
                    for (int row = rowIndexStart; row <= rowIndexEnd; row++) {
                        for (int col = colIndexStart; col <= colIndexEnd; col++) {
                            if (customTable.isCellSelected(row, col)) {
                                sb.append (CustomTableHelper.getColString(col+1) + (row+1).toString() + ";")
                                System.out.printf("Selected [Row,Column] = [%d,%d]\n", row, col)
                            }
                        }
                    }
                    sb.deleteCharAt(sb.length()-1)
                    cellEditDialog.dataTextField.text = sb.toString()
                }
            }
        })


        // Column Header Context Menu
        ULCPopupMenu colHeaderPopupMenu = new ULCPopupMenu()
        ULCMenuItem renameMenuItem = new ULCMenuItem("Rename")
        renameMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                def getName = { return pivotModel.customTableModel.getColumnName(lastLeftClickedColumn.getModelIndex()) }
                def setName = { name -> pivotModel.customTableModel.setColumnName (lastLeftClickedColumn.getModelIndex(), name) }
                createRenameDialog (getName, setName)
            }
        })
        colHeaderPopupMenu.add(renameMenuItem)
        customTable.getTableHeader().setComponentPopupMenu(colHeaderPopupMenu)

        customTable.getTableHeader().addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.modifiers == ActionEvent.META_MASK) {
                    lastLeftClickedColumn = (ULCTableColumn)actionEvent.source
                }
            }
        })

        // Row Header
        rowHeader = new ULCList(pivotModel.customTableModel.rowHeaderModel)
        rowHeader.setFixedCellWidth(50)
        rowHeader.setFixedCellHeight(customTable.getRowHeight())
        rowHeader.setCellRenderer(new CustomTableRowHeaderRenderer(customTable))

        // Row Header Context Menu
        ULCPopupMenu rowHeaderPopupMenu = new ULCPopupMenu()
        ULCMenuItem renameRowMenuItem = new ULCMenuItem("Rename")
        renameRowMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                def getName = { return pivotModel.customTableModel.rowHeaderModel.getElementAt (rowHeader.getSelectedIndex()).toString() }
                def setName = { name -> pivotModel.customTableModel.rowHeaderModel.set (rowHeader.getSelectedIndex(), name) }
                createRenameDialog (getName, setName)
            }
        })
        rowHeaderPopupMenu.add(renameRowMenuItem)
        rowHeader.setComponentPopupMenu(rowHeaderPopupMenu)

        // add table to the window
        ULCScrollPane newTableScrollPane = new ULCScrollPane(customTable)
        newTableScrollPane.setPreferredSize(new Dimension(300, 300))
        newTableScrollPane.setRowHeaderView(rowHeader)
        pane.add (ULCBoxPane.BOX_EXPAND_EXPAND, newTableScrollPane)

        // EditMode checkBox
        ULCBoxPane customTableButtonPane = new ULCBoxPane (false)

        ULCCheckBox editModeButton = new ULCCheckBox("Edit Mode")
        editModeButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                pivotModel.customTableModel.editMode = editModeButton.isSelected()
                pivotModel.customTableModel.fireTableDataChanged()
            }
        })
        customTableButtonPane.add (ULCBoxPane.BOX_CENTER_CENTER, editModeButton)

        ULCButton newRowButton = new ULCButton("Insert Row")
        newRowButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                pivotModel.customTableModel.addRow([], "")
                pivotModel.customTableModel.fireTableDataChanged()
            }
        })
        customTableButtonPane.add (ULCBoxPane.BOX_CENTER_CENTER, newRowButton)

        ULCButton newColButton = new ULCButton("Insert Column")
        newColButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                pivotModel.customTableModel.addCol ("")
                pivotModel.customTableModel.fireTableStructureChanged()
            }
        })
        customTableButtonPane.add (ULCBoxPane.BOX_CENTER_CENTER, newColButton)
        pane.add (ULCBoxPane.BOX_LEFT_CENTER, customTableButtonPane)

        return pane
    }

    /**
     * creates a Dialog for renaming a column or a row
     */
    public ULCDialog createRenameDialog (Closure getName, Closure setName) {
        ULCDialog renameDialog = new ULCDialog(parent, "Rename...", true)
        ULCBoxPane dialogPane = new ULCBoxPane (true)
        ULCTextField nameTextField = new ULCTextField (getName())
        dialogPane.add(ULCBoxPane.BOX_EXPAND_TOP, nameTextField)

        ULCButton okButton = new ULCButton ("OK")
        okButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                setName (nameTextField.getText())
                renameDialog.setVisible(false);
            }
        })

        dialogPane.add(ULCBoxPane.BOX_EXPAND_TOP, okButton)
        renameDialog.add (dialogPane)
        renameDialog.setVisible(true)

        return renameDialog
    }

    /**
     * TransferHandler for DnD for the CustomTable, to import Data from the PreviewTree
     */
    public class MyTransferHandler extends TransferHandler {
        @Override
        boolean importData(ULCComponent ulcComponent, Transferable transferable) {
            Object transferData = transferable.getTransferData(DataFlavor.DRAG_FLAVOR)

            if (transferData instanceof DnDTableTreeData) {
                TreePath[] treePaths = ((DnDTableTreeData)transferable.getTransferData(DataFlavor.DRAG_FLAVOR)).getTreePaths()

                for (TreePath treePath in treePaths) {
                    PreviewNode node = (PreviewNode)treePath.getLastPathComponent();

                    String colString = node.getValueAt(0)
                    int colIndex = pivotModel.customTableModel.columnNames.indexOf(colString)
                    if (colIndex < 0) {
                        colIndex = pivotModel.customTableModel.addCol (colString)
                    }

                    String rowString = node.parent.getPathString()
                    int rowIndex = pivotModel.customTableModel.rowHeaderModel.indexOf(rowString)
                    if (rowIndex < 0) {
                        rowIndex = pivotModel.customTableModel.addRow ([], rowString)
                    }

                    pivotModel.customTableModel.setValueAt(node.getValueAt(1), rowIndex, colIndex)
                }

            } else if (transferData instanceof DnDTableData) {

                ULCTable table = ((DnDTableData)transferable.getTransferData(DataFlavor.DRAG_FLAVOR)).getTable()
                for (int row : table.getSelectedRows()) {
                    List<Object> rowData = new LinkedList<Object>()
                    for (int col = 0; col < table.columnCount || col < pivotModel.customTableModel.columnCount; col++)
                        rowData.add (table.getValueAt(row, col))

                    pivotModel.customTableModel.addRow(rowData, "")
                }
            }

            return true
        }

        @Override
        void exportDone(ULCComponent ulcComponent, Transferable transferable, int i) {
        }
    }


}
