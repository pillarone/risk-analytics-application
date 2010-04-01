package org.pillarone.riskanalytics.application.ui.table.action

import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AddColumnAction extends TableAction {
    /**
     * columnPosition +1, add after the  actual selected column
     *  -1 add before
     */
    int columnPosition

    public AddColumnAction(ULCTable table, int columnPosition) {
        super(columnPosition == 0 ? "AddColumnActionBefore" : "AddColumnActionAfter", table);
        this.columnPosition = columnPosition;
    }

    void doActionPerformed(ActionEvent event) {
        int index = getColumnIndex() + columnPosition
        if (isMatrix()) {
            table.model.addColumnAndRow(index)
            table.updateCount(true, 1)
        } else {
            table.model.addColumnAt(index)
        }
        table.updateCount(false, 1)
    }
}

class RemoveColumnAction extends TableAction {

    public RemoveColumnAction(ULCTable table) {
        super("RemoveColumnAction", table);
    }

    void doActionPerformed(ActionEvent event) {
        // -1 for the index column
        int index = getColumnIndex()
        if (table.isMatrix()) {
            table.model.removeColumnAndRow(index)
            table.updateCount(true, -1)
        } else {
            table.model.removeColumnAt(index)
        }
        table.updateCount(false, -1)
    }
}


class AddRowAction extends TableAction {
    /**
     * rowPosition +1, add after the  actual selected row
     *  -1 add before
     */
    int rowPosition

    public AddRowAction(ULCTable table, int rowPosition) {
        super(rowPosition == 0 ? "AddRowActionBefore" : "AddRowActionAfter", table);
        this.rowPosition = rowPosition
    }

    void doActionPerformed(ActionEvent event) {
        int index = getRowIndex() + rowPosition
        if (isMatrix()) {
            table.model.addColumnAndRow(index)
            table.updateCount(false, 1)
        } else {
            table.model.addRowAt(index)
        }
        table.updateCount(true, 1)
    }
}

class RemoveRowAction extends TableAction {

    public RemoveRowAction(ULCTable table) {
        super("RemoveRowAction", table);
    }

    void doActionPerformed(ActionEvent event) {
        // -1 for the index column
        int index = getRowIndex()
        if (isMatrix()) {
            table.model.removeColumnAndRow(index)
            table.updateCount(false, -1)
        } else {
            table.model.removeRowAt(index)
        }
        table.updateCount(true, -1)
    }
}

class MoveRowAction extends TableAction {
    /**
     * rowPosition +1, add after the  actual selected row
     *  -1 add before
     */
    int rowPosition

    public MoveRowAction(ULCTable table, int rowPosition) {
        super(rowPosition == -1 ? "MoveRowToTop" : "MoveRowToBottom", table);
        this.rowPosition = rowPosition
    }

    void doActionPerformed(ActionEvent event) {
        int rowIndex = getRowIndex()
        if (!validate(rowIndex)) return;
        int targetIndex = rowIndex + rowPosition
        if (isMatrix()) {
            table.model.moveColumnAndRow(rowIndex, targetIndex)
        } else {
            table.model.moveRowTo(rowIndex, targetIndex)
        }
    }

    private boolean validate(int index) {
        return (rowPosition == 1) ? index < getRowCount() - 1 : index > 0;
    }

}

class MoveColumnAction extends TableAction {
    /**
     * columnPosition +1, add after the  actual selected column
     *  -1 add before
     */
    int columnPosition

    public MoveColumnAction(ULCTable table, int columnPosition) {
        super(columnPosition == 1 ? "MoveRight" : "MoveLeft", table);
        this.columnPosition = columnPosition;
    }

    void doActionPerformed(ActionEvent event) {
        int columnIndex = getColumnIndex()
        if (!validate(columnIndex)) return;
        int targetIndex = columnIndex + columnPosition
        if (isMatrix()) {
            table.model.moveColumnAndRow(columnIndex, targetIndex)
        } else {
            table.model.moveColumnTo(columnIndex, targetIndex)
        }
    }

    private boolean validate(int index) {
        return (columnPosition == 1) ? index < getColumnCount() - 1 : index > 0;
    }

}




abstract class TableAction extends ResourceBasedAction {

    ULCTable table

    public TableAction(String actionName, ULCTable table) {
        super(actionName);
        this.table = table;
    }

    protected boolean isMatrix() {
        return table.isMatrix()
    }

    protected int getColumnIndex() {
        // -2 : matrix  and index column
        //-1 : index column (Table)
        return isMatrix() ? table.selectedColumn - 2 : table.selectedColumn - 1
    }

    protected int getRowIndex() {
        //-1 : header (Table)
        return table.selectedRow - 1
    }

    protected int getColumnCount() {
        return isMatrix() ? table.getColumnCount() - 2 : table.getColumnCount() - 1;
    }

    protected int getRowCount() {
        return table.getRowCount() - 1;
    }

}

