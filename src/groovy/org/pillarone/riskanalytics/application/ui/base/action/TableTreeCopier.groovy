package org.pillarone.riskanalytics.application.ui.base.action

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.util.LocaleResources

import java.text.NumberFormat
import org.pillarone.riskanalytics.application.ui.util.UIUtils

class TableTreeCopier extends ExceptionSafeAction {

    ULCTableTree table
    ITableTreeModel model

    public TableTreeCopier() {
        super("Copy")
        putValue(IAction.SMALL_ICON, UIUtils.getIcon("copy-active.png"));
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, false));
    }

    public void doActionPerformed(ActionEvent event) {
        model = table.model
        int[] selectedRows = getSelectedRows()
        int[] selectedColumns = getSelectedColumns()
        if (selectedRows != null) {
            String content = copyContent(selectedRows, selectedColumns)
            ULCClipboard.getClipboard().content = content
        }
    }

    private int[] getSelectedColumns() {
        List list = table.selectedColumns?.collect {int column-> table.convertColumnIndexToModel(column) } as List
        return list as int[]
    }

    private int[] getSelectedRows() {
        return (table.selectedRows as List)?.sort() as int[]
    }

    private String copyContent(int[] selectedRows, int[] selectedColumns) {
        StringBuffer buffer = new StringBuffer()
        for (int i = 0; i < selectedRows.size(); i++) {
            TreePath path = table.getPathForRow(selectedRows[i])
            for (int j = 0; j < selectedColumns.size(); j++) {
                buffer << format(model.getValueAt(path.lastPathComponent, selectedColumns[j]))
                if (j != selectedColumns.size() - 1)
                    buffer << '\t'
            }
            buffer << '\n'
        }
        buffer.toString()
    }

    protected getCopyFormat() {
        NumberFormat format = NumberFormat.getInstance(LocaleResources.locale)
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