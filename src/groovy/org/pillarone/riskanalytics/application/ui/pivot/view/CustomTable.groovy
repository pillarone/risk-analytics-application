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
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CellEditDialog
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCListSelectionModel
import com.ulcjava.base.shared.UlcEventConstants
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CustomTableHelper
import org.pillarone.riskanalytics.application.ui.pivot.model.PivotModel
import com.ulcjava.base.application.dnd.DataFlavor
import com.ulcjava.base.application.dnd.Transferable


class CustomTable extends ULCTable {
    CustomTable customTable = this;

    PivotModel pivotModel
    CellEditDialog cellEditDialog

    public CustomTable (PivotModel pivotModel) {
        super (pivotModel.customTableModel)
        this.pivotModel = pivotModel
        initTable()
    }

    private void initTable() {
        this.setSelectionMode(ULCListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        this.setRowSelectionAllowed(false)
        this.setColumnSelectionAllowed(false)
        this.setCellSelectionEnabled(true)
        this.setPreferredScrollableViewportSize(new Dimension(300, 300))
        ClientContext.setModelUpdateMode(pivotModel.customTableModel, UlcEventConstants.SYNCHRONOUS_MODE);
        this.setTransferHandler(new MyTransferHandler())

        this.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.actionCommand == "mouseDoubleClick") {
                    cellEditDialog = new CellEditDialog(parent, pivotModel.customTableModel, customTable.getSelectedRow(), customTable.getSelectedColumn())
                    cellEditDialog.setVisible(true)
                }
            }
        })

        this.getSelectionModel().addListSelectionListener(new IListSelectionListener() {
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

                /*ULCTable table = ((DnDTableData)transferable.getTransferData(DataFlavor.DRAG_FLAVOR)).getTable()
                for (int row : table.getSelectedRows()) {
                    List<Object> rowData = new LinkedList<Object>()
                    for (int col = 0; col < table.columnCount || col < pivotModel.customTableModel.columnCount; col++)
                        rowData.add (table.getValueAt(row, col))

                    pivotModel.customTableModel.addRow(rowData, "")
                }        */
            }

            return true
        }

        @Override
        void exportDone(ULCComponent ulcComponent, Transferable transferable, int i) {
        }
    }

}
