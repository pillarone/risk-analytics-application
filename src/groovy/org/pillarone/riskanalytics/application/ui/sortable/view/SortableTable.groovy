package org.pillarone.riskanalytics.application.ui.sortable.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.dnd.DnDTableData
import com.ulcjava.base.application.dnd.TransferHandler
import com.ulcjava.base.application.dnd.Transferable
import org.pillarone.riskanalytics.application.ui.sortable.model.IOrderChangedListener
import org.pillarone.riskanalytics.application.ui.sortable.model.SortableTableModel
import org.pillarone.riskanalytics.application.ui.sortable.model.SortedEvent

import static com.ulcjava.base.application.dnd.DataFlavor.DRAG_FLAVOR
import static com.ulcjava.base.application.dnd.DataFlavor.DROP_FLAVOR

class SortableTable extends ULCTable {

    SortableTable(SortableTableModel model) {
        super(model)
        model.addOrderChangedListener(new MyOrderChangedListener())
        initialize()
    }

    @Override
    SortableTableModel getModel() {
        (SortableTableModel) super.model
    }

    @Override
    void setEnabled(boolean enabled) {
        super.setEnabled(enabled)
        dragEnabled = enabled
    }

    private initialize() {
        dragEnabled = true
        transferHandler = new MyTransferHandler()
    }

    private class MyTransferHandler extends TransferHandler {
        @Override
        boolean importData(ULCComponent targetComponent, Transferable transferable) {
            DnDTableData dragged = transferable.getTransferData(DRAG_FLAVOR) as DnDTableData
            DnDTableData dropped = transferable.getTransferData(DROP_FLAVOR) as DnDTableData
            getModel().moveFromTo(dragged.selectedRows, dropped.selectedRows[0])
        }

        @Override
        void exportDone(ULCComponent sourceComponent, Transferable transferable, int dropAction) {
        }
    }

    private class MyOrderChangedListener implements IOrderChangedListener {
        @Override
        void orderChanged(SortedEvent event) {
            getSelectionModel().clearSelection()
            event.newIndices.each { int index ->
                getSelectionModel().addSelectionInterval(index, index)
            }
        }
    }
}
