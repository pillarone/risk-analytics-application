package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.table.DefaultTableHeaderCellRenderer
import org.pillarone.riskanalytics.application.ui.batch.model.BatchRowInfoRowModel
import org.pillarone.riskanalytics.application.ui.batch.view.action.CreateBatchAction
import org.pillarone.riskanalytics.application.ui.batch.view.action.CreateSimulationProfileAction
import org.pillarone.riskanalytics.application.ui.batch.view.action.FindInTreeAction

class BatchTableRenderer extends DefaultTableCellRenderer {
    private ULCPopupMenu nodePopup
    private final BatchView batchView
    List<EnabledCheckingMenuItem> menuItems = []

    BatchTableRenderer(BatchView batchView) {
        this.batchView = batchView
        batchView.batches.selectionModel.addListSelectionListener(new IListSelectionListener() {
            @Override
            void valueChanged(ListSelectionEvent event) {
                menuItems.each { it.updateEnablingState() }
            }
        })
    }

    IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean selected, boolean hasFocus, int row) {
        IRendererComponent component = super.getTableCellRendererComponent(table, value, selected, hasFocus, row)
        toolTipText = String.valueOf(value)
        componentPopupMenu = nodePopUp
        horizontalAlignment = LEFT
        return component
    }

    private BatchRowInfoRowModel getModelAt(int row) {
        batchView.batchViewModel.simulationParameterizationTableModel.backedList[row]
    }

    private ULCPopupMenu getNodePopUp() {
        if (!nodePopup) {
            nodePopup = new ULCPopupMenu()
            EnabledCheckingMenuItem createSimulationProfileItem = new EnabledCheckingMenuItem(new CreateSimulationProfileAction(batchView))
            EnabledCheckingMenuItem findInTreeItem = new EnabledCheckingMenuItem(new FindInTreeAction(batchView))
            EnabledCheckingMenuItem createBatchItem = new EnabledCheckingMenuItem(new CreateBatchAction(batchView))
            menuItems << createSimulationProfileItem
            menuItems << findInTreeItem
            menuItems << createBatchItem
            nodePopup.add(createSimulationProfileItem)
            nodePopup.add(findInTreeItem)
            nodePopup.add(createBatchItem)
        }
        return nodePopup
    }
}

class BatchTableHeaderRenderer extends DefaultTableHeaderCellRenderer {

    IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean selected, boolean hasFocus, int column) {
        horizontalAlignment = CENTER
        super.getTableCellRendererComponent(table, value, selected, hasFocus, column)
    }
}
