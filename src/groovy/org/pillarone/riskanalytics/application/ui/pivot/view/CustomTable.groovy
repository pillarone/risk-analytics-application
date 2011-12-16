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
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.ResultNavigator
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor


class CustomTable extends ULCTable {
    PivotView pivotView

    PivotModel pivotModel
    CellEditDialog cellEditDialog

    public CustomTable (PivotModel pivotModel, PivotView pivotView) {
        super (pivotModel.customTableModel)
        this.pivotModel = pivotModel
        this.pivotView = pivotView
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
                    cellEditDialog = new CellEditDialog(pivotView.parent, pivotModel.customTableModel, CustomTable.this.getSelectedRow(), CustomTable.this.getSelectedColumn())
                    cellEditDialog.setVisible(true)
                }
            }
        })

        this.getSelectionModel().addListSelectionListener(new IListSelectionListener() {
            void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (cellEditDialog != null && cellEditDialog.isVisible() && cellEditDialog.selectDataMode) {
                    int rowIndexStart = CustomTable.this.getSelectedRow();
                    int rowIndexEnd = CustomTable.this.getSelectionModel().getMaxSelectionIndex();
                    int colIndexStart = CustomTable.this.getSelectedColumn();
                    int colIndexEnd = CustomTable.this.getColumnModel().getSelectionModel().getMaxSelectionIndex();

                    StringBuilder sb = new StringBuilder()
                    for (int row = rowIndexStart; row <= rowIndexEnd; row++) {
                        for (int col = colIndexStart; col <= colIndexEnd; col++) {
                            if (CustomTable.this.isCellSelected(row, col)) {
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

                ULCTable table = ((DnDTableData)transferable.getTransferData(DataFlavor.DRAG_FLAVOR)).getTable()
                if (pivotView.resultNavigator != null) {
                    return true;
                    // TODO: simRun property in ResultNavigator
//                    SimulationRun simRun = pivotView.resultNavigator.simRun
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
        void exportDone(ULCComponent ulcComponent, Transferable transferable, int i) {
        }
    }

}
