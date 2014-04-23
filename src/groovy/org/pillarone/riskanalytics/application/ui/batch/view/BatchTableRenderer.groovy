package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.table.DefaultTableHeaderCellRenderer

class BatchTableRenderer extends DefaultTableCellRenderer {
    private ULCPopupMenu nodePopup

    IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean selected, boolean hasFocus, int column) {
        IRendererComponent component = super.getTableCellRendererComponent(table, value, selected, hasFocus, column)
        toolTipText = String.valueOf(value)
        componentPopupMenu = getNodePopUp(table)
        horizontalAlignment = LEFT
        return component
    }

    private ULCPopupMenu getNodePopUp(ULCTable table) {
        if (!nodePopup) {
            nodePopup = new ULCPopupMenu()
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
