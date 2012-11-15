package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.dnd.TransferHandler
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.dnd.Transferable
import com.ulcjava.base.application.dnd.DnDTableData
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.OutputElementTable
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElementTableModel
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement
import com.ulcjava.base.application.dnd.DataFlavor
import com.ulcjava.base.application.dnd.DnDLabelData
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableHelper
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.shared.IWindowConstants
import com.ulcjava.base.application.ULCAlert

/**
 * TransferHandler for DnD for the CustomTable, to import Data from the PreviewTree
 *
 * @author ivo.nussbaumer
 */
class CustomTableDropHandler extends TransferHandler {
    @Override
    public boolean importData(ULCComponent ulcComponent, Transferable transferable) {
        Object dragData = transferable.getTransferData(DataFlavor.DRAG_FLAVOR)
        Object dropData = transferable.getTransferData(DataFlavor.DROP_FLAVOR)

        if (dropData instanceof DnDTableData) {
            CustomTable customTable = (CustomTable) dropData.getTable()
            // insert data from the result navigator
            if (dragData instanceof DnDTableData && dropData instanceof DnDTableData) {
                OutputElementTable table = (OutputElementTable) dragData.getTable()
                OutputElementTableModel tableModel = (OutputElementTableModel) table.getModel()
                int dropRowOrigin = dropData.getSelectedRows()[0]
                int dropColOrigin = dropData.getSelectedColumns()[0]

                int dropRow = dropRowOrigin
                int dropCol = dropColOrigin

                for (int dragRow : table.getSelectedRows()) {
                    DataCellElement dataCellElement = new DataCellElement(tableModel.getRowElement(table.convertRowIndexToModel(dragRow)),
                            table.keyfigureSelection.period,
                            table.keyfigureSelection.keyfigure,
                            table.keyfigureSelection.keyfigureParameter)
                    if (dataCellElement.run.name != customTable.customTableView.getSimulationRunName()) {
                        //Can't drop data from simrun_A onto simRun_B result.. ART-1000
                        //Can we actually assume all simruns are the same for every cell? If not, what happens if this thing blows up in mid-import?
                        ULCAlert alert = new ULCAlert("Invalid drag operation", "<HTML>Cannot drop data from<BR><B>" + dataCellElement.run.name + "</B> result onto a <BR><B>" + customTable.customTableView.getSimulationRunName() + "</B> result table</HTML> ", "Ok")
                        alert.show()
                        return false
                    }
                    customTable.customTableModel.setValueAt(dataCellElement, dropRow++, dropCol)

                    if (dropRow >= customTable.rowCount) {
                        dropRow = 0
                        dropCol++

                        if (dropCol >= customTable.columnCount) {
                            dropCol = 0
                        }
                    }
                }

                customTable.selectionModel.setSelectionInterval(dropRowOrigin, dropRowOrigin)
                customTable.columnModel.selectionModel.setSelectionInterval(dropColOrigin, dropColOrigin)
            }

            // insert category values from the comboBox in the DataCellEditPane
            if (dragData instanceof DnDLabelData && dropData instanceof DnDTableData) {
                // get the comboBox with the values
                ULCComboBox combo = customTable.customTableView.dataCellEditPane.categoryComboBoxes[dragData.getLabel().getName()]

                // copy the values of the combo in a list
                List<String> categoryValues = new LinkedList<String>()
                for (int i = 0; i < combo.getItemCount(); i++) {
                    categoryValues.add(combo.getItemAt(i).toString())
                }

                int dropRow = dropData.getSelectedRows()[0]
                int dropCol = dropData.getSelectedColumns()[0]

                // show category value insert dialog
                CategoryValuesInsertDialog dlg = new CategoryValuesInsertDialog(customTable.customTableView.parent,
                        CustomTableHelper.getVariable(dropRow, dropCol),
                        categoryValues,
                        customTable.customTableModel.getDataAt(dropRow + 1, dropCol) == "")
                dlg.toFront()
                dlg.locationRelativeTo = UlcUtilities.getWindowAncestor(customTable.customTableView.parent)
                dlg.defaultCloseOperation = IWindowConstants.DISPOSE_ON_CLOSE
                dlg.visible = true

                // when dialog is closed, insert the values into the table
                dlg.addWindowListener(new IWindowListener() {
                    void windowClosing(WindowEvent windowEvent) {
                        if (!dlg.isCancel) {
                            dropRow = CustomTableHelper.getRow(dlg.getStartCell())
                            dropCol = CustomTableHelper.getCol(dlg.getStartCell())

                            // Change value in comboBox on DataCellEditPane to the variable
                            if (dlg.isVertical())
                                combo.setSelectedItem("=\$" + dlg.getStartCell())
                            else
                                combo.setSelectedItem("=" + CustomTableHelper.getColString(dropCol + 1) + "\$" + (dropRow + 1).toString())

                            // insert values into the table
                            for (String categoryValue : dlg.getValues()) {
                                customTable.customTableModel.setValueAt(categoryValue, dropRow, dropCol)
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
        return true
    }

    @Override
    public void exportDone(ULCComponent ulcComponent, Transferable transferable, int i) {
    }
}
