package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.table.DefaultTableHeaderCellRenderer
import org.pillarone.riskanalytics.application.ui.batch.model.BatchRowInfoRowModel
import org.pillarone.riskanalytics.application.ui.batch.view.action.*

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
            addItem(new CreateSimulationProfileAction(batchView))
            addItem(new FindInTreeAction(batchView))
            addItem(new CreateBatchAction(batchView))
            addItem(new BatchOpenParameterizationAction(batchView))
            addItem(new BatchOpenresultAction(batchView))
            addItem(new OpenResultsAction(batchView))
            addItem(new DeleteParameterizationsAction(batchView))
        }
        return nodePopup
    }

    private void addItem(IAction action) {
        EnabledCheckingMenuItem menuItem = new EnabledCheckingMenuItem(action)
        menuItems << menuItem
        nodePopup.add(menuItem)
    }
}

class BatchTableHeaderRenderer extends DefaultTableHeaderCellRenderer {

    IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean selected, boolean hasFocus, int column) {
        horizontalAlignment = CENTER
        super.getTableCellRendererComponent(table, value, selected, hasFocus, column)
    }
}
