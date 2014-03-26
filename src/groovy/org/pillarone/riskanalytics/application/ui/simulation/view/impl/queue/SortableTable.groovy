package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.dnd.DnDTableData
import com.ulcjava.base.application.dnd.TransferHandler
import com.ulcjava.base.application.dnd.Transferable
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SortableTableModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SortedEvent

import static com.ulcjava.base.application.dnd.DataFlavor.DRAG_FLAVOR
import static com.ulcjava.base.application.dnd.DataFlavor.DROP_FLAVOR

class SortableTable extends ULCTable implements SortableTableModel.IOrderChangedListener {

    SortableTable(SortableTableModel model) {
        super(model)
        model.addOrderChangedListener(this)
        initialize()
    }

    @Override
    SortableTableModel getModel() {
        (SortableTableModel) super.model
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
            model.moveFromTo(dragged.selectedRows, dropped.selectedRows[0])
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
