package org.pillarone.riskanalytics.application.ui.base.action

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tree.TreePath
import java.text.NumberFormat
import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.application.ui.util.UIUtils

class TableTreeCopier extends ExceptionSafeAction {

    ULCTableTree table
    ITableTreeModel model
    int rowCount
    int columnCount

    public TableTreeCopier() {
        super("Copy")
        putValue(IAction.SMALL_ICON, UIUtils.getIcon("copy-active.png"));
    }

    public void doActionPerformed(ActionEvent event) {
        model = table.model
        def int[] selectedRows = getSelectedRows()
        def int[] selectedColumns = getSelectedColumns()
        if (selectedRows != null) {
            int startRow = selectedRows[0]
            int startColumn = selectedColumns[0]
            rowCount = selectedRows.size()
            columnCount = selectedColumns.size()
            String content = copyContent(startRow, startColumn)
            ULCClipboard.getClipboard().content = content
        }
    }

    private int[] getSelectedColumns() {
        return (table.selectedColumns as List)?.sort() as int[]
    }

    private int[] getSelectedRows() {
        return (table.selectedRows as List)?.sort() as int[]
    }

    private String copyContent(int startRow, int startColumn) {
        StringBuffer buffer = new StringBuffer()
        for (int i = startRow; i < (startRow + rowCount); i++) {
            TreePath path = table.getPathForRow(i)
            for (int j = startColumn; j < (startColumn + columnCount); j++) {

                buffer << format(model.getValueAt(path.lastPathComponent, j + 1))
                buffer << '\t'
            }
            buffer << '\n'
        }
        buffer.toString()
    }

    protected getCopyFormat() {
        NumberFormat format = NumberFormat.getInstance(ClientContext.getLocale())
        format.setMaximumFractionDigits(10)
        format.groupingUsed = false
        return format
    }

    protected String format(Object o) {
        if (o) {
            return o.toString()
        }
        else {
            return ""
        }
    }

    protected String format(Number n) {
        return copyFormat.format(n)
    }
}