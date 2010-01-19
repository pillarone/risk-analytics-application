package org.pillarone.riskanalytics.application.ui.base.action

import com.canoo.ulc.community.ulcclipboard.server.IClipboardHandler
import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.table.ITableModel
import com.ulcjava.base.application.util.Cursor
import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.application.ui.base.model.IBulkChangeable
import org.pillarone.riskanalytics.application.ui.util.TableDataParser
import org.pillarone.riskanalytics.application.ui.util.UIUtils

class TablePaster extends ExceptionSafeAction {

    ULCTable table
    ITableModel model
    int rowCount
    int columnCount

    public TablePaster() {
        putValue(IAction.SMALL_ICON, UIUtils.getIcon("paste-active.png"));
    }

    public void doActionPerformed(ActionEvent event) {
        int startRow = table.getSelectedRow()
        int startColumn = table.getSelectedColumn()
        rowCount = table.rowCount
        columnCount = table.columnCount

        ULCComponent parent = UlcUtilities.getWindowAncestor(table)
        parent?.cursor = Cursor.WAIT_CURSOR

        ULCClipboard.appyClipboardContent([applyContent: {String clipboardContent ->
            pasteContent(clipboardContent, startRow, startColumn)
        }] as IClipboardHandler)


    }

    void pasteContent(String content, int startRow, int startColumn) {
        if (content) {
            List data = new TableDataParser().parseTableData(content)
            withBulkChange(model) {
                data.eachWithIndex {line, lineIndex ->
                    line.eachWithIndex {cellValue, cellIndex ->
                        int colIndex = startColumn + cellIndex
                        int rowIndex = startRow + lineIndex
                        if (rowIndex < rowCount && colIndex < columnCount && model.isCellEditable(rowIndex, colIndex)) {
                            model.setValueAt(data[lineIndex][cellIndex], rowIndex, colIndex)
                        }
                    }
                }

            }
        }
    }

    private withBulkChange(ITableModel tableModel, Closure change) {
        startBulkChange(tableModel)
        change.call()
        stopBulkChange(tableModel)
    }

    private void startBulkChange(ITableModel tableModel) {
        if (tableModel instanceof IBulkChangeable) {
            tableModel.startBulkChange()
        }
    }

    private void stopBulkChange(ITableModel tableModel) {
        ULCComponent parent = UlcUtilities.getWindowAncestor(table)
        parent?.cursor = Cursor.DEFAULT_CURSOR
        if (tableModel instanceof IBulkChangeable) {
            tableModel.stopBulkChange()
        }
    }
}