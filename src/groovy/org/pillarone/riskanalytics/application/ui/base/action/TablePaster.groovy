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
import org.pillarone.riskanalytics.application.ui.base.model.IBulkChangeable
import org.pillarone.riskanalytics.application.ui.table.view.MultiDimensionalTable
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
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
        if (startRow == 0 || startColumn == 0) {
            if (table instanceof MultiDimensionalTable) return
        }
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
                if (!validate(data, startColumn)) {
                    showAlert(table)
                    return
                }
                data.eachWithIndex {line, lineIndex ->
                    line.eachWithIndex {cellValue, cellIndex ->
                        int colIndex = startColumn + cellIndex
                        int rowIndex = startRow + lineIndex
                        updateTableCount(rowIndex, colIndex)
                        if (model.isCellEditable(rowIndex, colIndex)) {
                            model.setValueAt(data[lineIndex][cellIndex], rowIndex, colIndex)
                        }
                    }
                }

            }
        }
    }

    private void showAlert(ULCTable table) {
        try {
            new I18NAlert(UlcUtilities.getWindowAncestor(table), "columnCountError").show()
        } catch (Exception ex) {}
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

    private void updateTableCount(int rowIndex, int colIndex) {
        if (!table) return
        if (rowIndex >= rowCount)
            model.addRowAt(rowIndex)
        if (colIndex >= columnCount)
            model.addColumnAt(colIndex)
        table.updateCount(true, table.rowCount - rowCount)
        table.updateCount(false, table.columnCount - columnCount)
        rowCount = table.rowCount
        columnCount = table.columnCount
    }

    private boolean validate(List data, int startColumn) {
        boolean status = true
        data.eachWithIndex {line, lineIndex ->
            line.eachWithIndex {cellValue, cellIndex ->
                int colIndex = startColumn + cellIndex
                if (colIndex >= columnCount) {
                    status = false
                }
            }
        }
        return status
    }
}