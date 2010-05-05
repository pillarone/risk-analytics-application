package org.pillarone.riskanalytics.application.ui.table.view

import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterTableModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalParameterView
import org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.table.action.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MultiDimensionalTable extends ULCTable {

    final ULCPopupMenu rowPopupMenu
    final ULCPopupMenu columnPopupMenu

    final ULCMenuItem addRowAfterMenuItem
    final ULCMenuItem addRowBeforeMenuItem
    final ULCMenuItem removeRowMenuItem
    final ULCMenuItem addColumnAfterMenuItem
    final ULCMenuItem addColumnBeforeMenuItem
    final ULCMenuItem removeColumnMenuItem
    //move
    final ULCMenuItem moveRightColumnMenuItem
    final ULCMenuItem moveLeftColumnMenuItem
    final ULCMenuItem moveTopRowMenuItem
    final ULCMenuItem moveBottomRowMenuItem

    private AddColumnAction addColumnAfterAction
    private AddColumnAction addColumnBeforeAction
    private RemoveColumnAction removeColumnAction
    private AddRowAction addRowAfterAction
    private AddRowAction addRowBeforeAction
    private RemoveRowAction removeRowAction
    private MoveColumnAction moveRightColumnAction
    private MoveColumnAction moveLeftColumnAction
    private MoveRowAction moveTopRowAction
    private MoveRowAction moveBottomRowAction

    MultiDimensionalParameterView multiDimensionalParameterView

    public MultiDimensionalTable(MultiDimensionalParameterView multiDimensionalParameterView, MultiDimensionalParameterTableModel model) {
        super.setModel(model)
        this.multiDimensionalParameterView = multiDimensionalParameterView
        initPopupMenu()
        setSelectionMode(ULCListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        setRowSelectionAllowed(true)
        setColumnSelectionAllowed(true)

        getSelectionModel().addListSelectionListener([valueChanged: {ListSelectionEvent event ->
            ULCListSelectionModel source = (ULCListSelectionModel) event.getSource()
            int row = source.getMinSelectionIndex()
            int column = columnModel.getSelectionModel().getMinSelectionIndex()
            if (column == 0) {
                columnModel.getSelectionModel().addSelectionInterval(1, getColumnCount() - 1)
            }
            if (row == 0 && getRowCount() > 1) {
                addRowSelectionInterval(1, getRowCount() - 1)
            }
        }] as IListSelectionListener)

        columnModel.getSelectionModel().addListSelectionListener([valueChanged: {ListSelectionEvent event ->
            ULCListSelectionModel source = (ULCListSelectionModel) event.getSource()
            int column = source.getMinSelectionIndex()
            int row = getSelectionModel().getMinSelectionIndex()
            if (row == 0 && getRowCount() > 1) {
                addRowSelectionInterval(1, getRowCount() - 1)
            }
            if (column == 0) {
                columnModel.getSelectionModel().addSelectionInterval(1, getColumnCount() - 1)
            }
        }] as IListSelectionListener)
    }


    private ULCMenuItem initPopupMenu() {
        rowPopupMenu = new ULCPopupMenu();
        columnPopupMenu = new ULCPopupMenu();

        addColumnBeforeAction = new AddColumnAction(this, 0)
        addColumnAfterAction = new AddColumnAction(this, +1)
        removeColumnAction = new RemoveColumnAction(this)
        addRowBeforeAction = new AddRowAction(this, 0)
        addRowAfterAction = new AddRowAction(this, +1)
        removeRowAction = new RemoveRowAction(this)
        moveRightColumnAction = new MoveColumnAction(this, 1)
        moveLeftColumnAction = new MoveColumnAction(this, -1)
        moveTopRowAction = new MoveRowAction(this, -1)
        moveBottomRowAction = new MoveRowAction(this, 1)

        addRowAfterMenuItem = createMenuItem(addRowAfterAction)
        addRowBeforeMenuItem = createMenuItem(addRowBeforeAction)
        removeRowMenuItem = createMenuItem(removeRowAction)

        addColumnAfterMenuItem = createMenuItem(addColumnAfterAction)
        addColumnBeforeMenuItem = createMenuItem(addColumnBeforeAction)
        removeColumnMenuItem = createMenuItem(removeColumnAction)
        //move
        moveRightColumnMenuItem = createMenuItem(moveRightColumnAction)
        moveLeftColumnMenuItem = createMenuItem(moveLeftColumnAction)
        moveTopRowMenuItem = createMenuItem(moveTopRowAction)
        moveBottomRowMenuItem = createMenuItem(moveBottomRowAction)

        if (model.columnCountChangeable()) {
            columnPopupMenu.add(addColumnBeforeMenuItem)
            columnPopupMenu.add(addColumnAfterMenuItem)
            if (isMatrix()) {
                columnPopupMenu.addSeparator()
                columnPopupMenu.add(moveRightColumnMenuItem)
                columnPopupMenu.add(moveLeftColumnMenuItem)
            }
            columnPopupMenu.addSeparator()
            columnPopupMenu.add(removeColumnMenuItem)
        } else if (isMatrix()) {
            columnPopupMenu.add(moveRightColumnMenuItem)
            columnPopupMenu.add(moveLeftColumnMenuItem)
        }

        rowPopupMenu.add(addRowBeforeMenuItem)
        rowPopupMenu.add(addRowAfterMenuItem)
        rowPopupMenu.addSeparator()
        rowPopupMenu.add(moveBottomRowMenuItem)
        rowPopupMenu.add(moveTopRowMenuItem)
        rowPopupMenu.addSeparator()
        rowPopupMenu.add(removeRowMenuItem)
    }

    public ULCPopupMenu getPopupMenu(int row, int column) {
        if (row == 0 && column == 0) return null
        if (row == 0 && (column == 1 && (model.multiDimensionalParam instanceof ComboBoxMatrixMultiDimensionalParameter))) return null
        if (row == 0) return columnPopupMenu
        if (column == 0) return rowPopupMenu
        return null
    }

    private ULCMenuItem createMenuItem(ResourceBasedAction action) {
        ULCMenuItem menuItem = createMenuItem(((AbstractAction) action));
        menuItem.setName(action.getActionName());
        return menuItem;
    }

    private ULCMenuItem createMenuItem(AbstractAction action) {
        return new ULCMenuItem(action);
    }

    public void updateCount(boolean isRow, int x) {
        multiDimensionalParameterView.updateCount(isRow, x)
    }

    public boolean isMatrix() {
        return multiDimensionalParameterView.isMatrix()
    }

}

