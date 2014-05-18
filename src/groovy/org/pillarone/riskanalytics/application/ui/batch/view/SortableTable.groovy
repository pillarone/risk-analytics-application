package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.dnd.DnDTableData
import com.ulcjava.base.application.dnd.TransferHandler
import com.ulcjava.base.application.dnd.Transferable
import org.pillarone.riskanalytics.application.ui.batch.model.IOrderChangedListener
import org.pillarone.riskanalytics.application.ui.batch.model.SortableTableModel
import org.pillarone.riskanalytics.application.ui.batch.model.SortedEvent

import static com.ulcjava.base.application.dnd.DataFlavor.DRAG_FLAVOR
import static com.ulcjava.base.application.dnd.DataFlavor.DROP_FLAVOR

class SortableTable extends ULCTable implements IOrderChangedListener {

    SortableTable(SortableTableModel model) {
        super(model)
        model.addOrderChangedListener(this)
        initialize()
    }

    @Override
    SortableTableModel getModel() {
        (SortableTableModel) super.model
    }

    @Override
    void setEnabled(boolean enabled) {
        super.setEnabled(enabled)
        dragEnabled = false
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

    @Override
    void orderChanged(SortedEvent event) {
        selectionModel.clearSelection()
        event.newIndices.each {
            selectionModel.addSelectionInterval(it, it)
        }
    }
}
