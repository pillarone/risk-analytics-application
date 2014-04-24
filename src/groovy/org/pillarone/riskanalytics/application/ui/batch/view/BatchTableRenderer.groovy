package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.table.DefaultTableHeaderCellRenderer
import org.pillarone.riskanalytics.application.ui.batch.model.BatchRowInfoRowModel
import org.pillarone.riskanalytics.application.ui.batch.model.SimulationParameterizationTableModel

class BatchTableRenderer extends DefaultTableCellRenderer {
    private ULCPopupMenu nodePopup
    private final SimulationParameterizationTableModel simulationParameterizationTableModel

    BatchTableRenderer(SimulationParameterizationTableModel simulationParameterizationTableModel) {
        this.simulationParameterizationTableModel = simulationParameterizationTableModel
    }

    IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean selected, boolean hasFocus, int row) {
        IRendererComponent component = super.getTableCellRendererComponent(table, value, selected, hasFocus, row)
        toolTipText = String.valueOf(value)
        componentPopupMenu = getNodePopUp(table)
        horizontalAlignment = LEFT
        return component
    }

    private BatchRowInfoRowModel getModelAt(int row) {
        simulationParameterizationTableModel.backedList[row]
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
