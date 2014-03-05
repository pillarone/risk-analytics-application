package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.dnd.DnDTableData
import com.ulcjava.base.application.dnd.TransferHandler
import com.ulcjava.base.application.dnd.Transferable
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationQueuePaneModel

import static com.ulcjava.base.application.dnd.DataFlavor.DRAG_FLAVOR
import static com.ulcjava.base.application.dnd.DataFlavor.DROP_FLAVOR

class SimulationQueuePane {

    private ULCBoxPane content

    private ULCTable simulationTable

    private final SimulationQueuePaneModel model

    SimulationQueuePane(SimulationQueuePaneModel model) {
        this.model = model
        initialize()
    }

    private initialize() {
        content = new ULCBoxPane()
        simulationTable = new ULCTable(model.simulationTableModel)
        simulationTable.dragEnabled = true
        simulationTable.transferHandler = new MyTransferHandler()
        ULCScrollPane scrollPane = new ULCScrollPane()
        scrollPane.verticalScrollBar.blockIncrement = 100
        scrollPane.add(simulationTable)
        content.add(scrollPane)
    }

    ULCComponent getContent() {
        content
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
}
