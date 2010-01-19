package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.table.ITableModel
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe

class TableSelectionFiller implements IActionListener {

    ULCTable table
    ITableModel model

    public void actionPerformed(ActionEvent event) {
        ExceptionSafe.protect {
            int startColumn = table.getSelectedColumn()

            table.selectedRows.each {int index ->
                def value = model.getValueAt(index, startColumn)

                table.selectedColumns.each {int colIndex ->
                    if (colIndex > startColumn) {
                        model.setValueAt(value, index, colIndex)
                    }
                }
            }
        }
    }

}