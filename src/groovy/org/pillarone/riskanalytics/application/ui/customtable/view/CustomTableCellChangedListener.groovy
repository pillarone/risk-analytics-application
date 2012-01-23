package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.event.IListSelectionListener
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableHelper
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import com.ulcjava.base.application.ULCListSelectionModel

/**
 * CellChangedListener for the CustomTable
 *
 * @author ivo.nussbaumer
 */
public class CustomTableCellChangedListener implements IListSelectionListener {
    private CustomTable customTable
    public CustomTableCellChangedListener(CustomTable customTable) {
        this.customTable = customTable
    }
    void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (customTable.customTableView.cellEditTextField.selectDataMode) {
            // If selectDataMode is on
            // get the selected Cells, and insert them in the CellEditTextField
            int rowIndexStart = customTable.getSelectedRow();
            int rowIndexEnd   = customTable.getSelectionModel().getMaxSelectionIndex();
            int colIndexStart = customTable.getSelectedColumn();
            int colIndexEnd   = customTable.getColumnModel().getSelectionModel().getMaxSelectionIndex();

            StringBuilder sb = new StringBuilder()

            if (customTable.selectedRowCount == 1 && customTable.selectedColumnCount == 1) {
                sb.append(CustomTableHelper.getColString(colIndexStart + 1))
                sb.append((rowIndexStart + 1).toString())
            } else {
                if (colIndexStart != 0 || colIndexEnd != customTable.columnCount - 1) sb.append(CustomTableHelper.getColString(colIndexStart + 1))
                if (rowIndexStart != 0 || rowIndexEnd != customTable.rowCount - 1) sb.append((rowIndexStart + 1).toString())
                sb.append(":")
                if (colIndexStart != 0 || colIndexEnd != customTable.columnCount - 1) sb.append(CustomTableHelper.getColString(colIndexEnd + 1))
                if (rowIndexStart != 0 || rowIndexEnd != customTable.rowCount - 1) sb.append((rowIndexEnd + 1).toString())
            }

            customTable.customTableView.cellEditTextField.insertData(sb.toString())

        } else {
            Object cellData = customTable.customTableModel.getDataAt(customTable.getSelectedRow(), customTable.getSelectedColumn())

            if (cellData instanceof OutputElement) {
                customTable.customTableView.cellEditTextField.editable = false
                customTable.customTableView.dataCellEditPane.setVisible(true)
                customTable.customTableView.dataCellEditPane.setData (customTable.getSelectedRow(), customTable.getSelectedColumn())
                customTable.customTableView.cellEditTextField.setText(customTable.getSelectedRow(), customTable.getSelectedColumn())

            } else {
                // If the selectDataMode is off
                // set the Value of the cell into the cellEditTextField
                customTable.customTableView.cellEditTextField.editable = true
                customTable.customTableView.dataCellEditPane.setVisible(false)
                customTable.customTableView.cellEditTextField.setText(customTable.getSelectedRow(), customTable.getSelectedColumn())
            }
        }
    }
}
