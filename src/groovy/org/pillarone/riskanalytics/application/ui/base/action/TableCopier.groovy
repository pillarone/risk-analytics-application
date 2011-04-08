package org.pillarone.riskanalytics.application.ui.base.action

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.table.ITableModel
import java.text.NumberFormat
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataTableModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources

class TableCopier extends ExceptionSafeAction {

    ULCTable table
    ITableModel model

    public TableCopier() {
        putValue(IAction.SMALL_ICON, UIUtils.getIcon("copy-active.png"));
    }

    public void doActionPerformed(ActionEvent event) {
        model = table.model
        int[] selectedRows = getSelectedRows()
        int[] selectedColumns = getSelectedColumns()
        String content = copyContent(selectedRows, selectedColumns)
        ULCClipboard.getClipboard().content = content
    }

    public String copyContent(int[] selectedRows, int[] selectedColumns) {
        StringBuilder buffer = new StringBuilder()
        copyHeader(model, buffer, selectedColumns)
        for (int i = 0; i < selectedRows.size(); i++) {
            for (int j = 0; j < selectedColumns.size(); j++) {
                Object value = model.getValueAt(selectedRows[i], selectedColumns[j])
                buffer << format(value)
                if (j != selectedColumns.size() - 1)
                    buffer << '\t'
            }
            buffer << '\n'
        }
        buffer.toString()
    }

    private void copyHeader(def model, StringBuilder builder, int[] selectedColumns) {
    }

    private void copyHeader(ResultIterationDataTableModel iterationDataTableModel, StringBuilder builder, int[] selectedColumns) {
        //copy a header only by iteration s view
        for (int i = 0; i < selectedColumns.size(); i++) {
            builder << iterationDataTableModel.columnHeaders.get(selectedColumns[i])
            if (i != selectedColumns.size() - 1)
                builder << '\t'
        }
        builder << '\n'
    }

    private int[] getSelectedColumns() {
        return (table.selectedColumns as List)?.sort() as int[]
    }

    private int[] getSelectedRows() {
        return (table.selectedRows as List)?.sort() as int[]
    }


    protected String format(Number value) {
        return copyFormat.format(value)
    }

    protected String format(def value) {
        return String.valueOf(value)
    }

    protected getCopyFormat() {
        NumberFormat format = NumberFormat.getInstance(LocaleResources.getLocale())
        format.setMaximumFractionDigits(30)
        format.groupingUsed = false
        return format
    }
}