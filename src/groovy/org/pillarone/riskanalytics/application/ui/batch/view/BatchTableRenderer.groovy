package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.batch.action.*

class BatchTableRenderer extends DefaultTableCellRenderer {
    private ULCPopupMenu nodePopup
    private final BatchView batchView
    private List<EnabledCheckingMenuItem> menuItems = []

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

    private ULCPopupMenu getNodePopUp() {
        if (!nodePopup) {
            nodePopup = new ULCPopupMenu()
            addItem(new CreateSimulationProfileAction(batchView))
            addItem(new SelectParameterizationsInTreeAction(batchView))
            addItem(new SelectSimulationsInTreeAction(batchView))
            addItem(new CreateBatchAction(batchView))
            addItem(new BatchViewOpenItemAction(batchView))
            addItem(new BatchViewOpenResultAction(batchView))
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


