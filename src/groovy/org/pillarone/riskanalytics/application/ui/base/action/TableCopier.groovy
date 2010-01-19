package org.pillarone.riskanalytics.application.ui.base.action

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.table.ITableModel
import java.text.NumberFormat
import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.application.ui.util.UIUtils

class TableCopier extends ExceptionSafeAction {

    ULCTable table
    ITableModel model
    int rowCount
    int columnCount

    public TableCopier() {
        putValue(IAction.SMALL_ICON, UIUtils.getIcon("copy-active.png"));
    }

    public void doActionPerformed(ActionEvent event) {
        model = table.model
        int startRow = table.getSelectedRow()
        int startColumn = table.getSelectedColumn()
        rowCount = table.selectedRowCount
        columnCount = table.selectedColumnCount
        String content = copyContent(startRow, startColumn)
        ULCClipboard.getClipboard().content = content
    }

    private String copyContent(int startRow, int startColumn) {
        StringBuffer buffer = new StringBuffer()
        for (int i = startRow; i < (startRow + rowCount); i++) {
            for (int j = startColumn; j < (startColumn + columnCount); j++) {
                Object value = model.getValueAt(i, j)
                buffer << format(value)
                buffer << '\t'
            }
            buffer << '\n'
        }
        buffer.toString()
    }

    protected String format(Number value) {
        return copyFormat.format(value)
    }

    protected String format(def value) {
        return String.valueOf(value)
    }

    protected getCopyFormat() {
        NumberFormat format = NumberFormat.getInstance(ClientContext.getLocale())
        format.setMaximumFractionDigits(30)
        format.groupingUsed = false
        return format
    }
}