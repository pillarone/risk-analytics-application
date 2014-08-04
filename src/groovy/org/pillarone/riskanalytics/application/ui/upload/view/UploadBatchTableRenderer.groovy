package org.pillarone.riskanalytics.application.ui.upload.view

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import org.pillarone.riskanalytics.application.ui.upload.action.*
import org.pillarone.riskanalytics.application.ui.util.EnabledCheckingMenuItem

class UploadBatchTableRenderer extends DefaultTableCellRenderer {
    private ULCPopupMenu nodePopup
    private final UploadBatchView uploadBatchView
    private List<EnabledCheckingMenuItem> menuItems = []

    UploadBatchTableRenderer(UploadBatchView uploadBatchView) {
        this.uploadBatchView = uploadBatchView
        uploadBatchView.simulations.selectionModel.addListSelectionListener(new IListSelectionListener() {
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
            addItem(new SelectParameterizationsInTreeAction(uploadBatchView))
            addItem(new SelectSimulationsInTreeAction(uploadBatchView))
            addItem(new OpenParameterizationAction(uploadBatchView))
            addItem(new BatchViewOpenResultAction(uploadBatchView))
            addItem(new OpenResultsAction(uploadBatchView))
            addItem(new DeleteSimulationsAction(uploadBatchView))
        }
        return nodePopup
    }

    private void addItem(IAction action) {
        EnabledCheckingMenuItem menuItem = new EnabledCheckingMenuItem(action)
        menuItems << menuItem
        nodePopup.add(menuItem)
    }
}


