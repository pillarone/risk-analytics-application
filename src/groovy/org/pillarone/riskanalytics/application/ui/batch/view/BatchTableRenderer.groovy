package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.table.DefaultTableHeaderCellRenderer
import org.pillarone.riskanalytics.application.ui.batch.action.ChangeBatchSimulationPriorityAction
import org.pillarone.riskanalytics.application.ui.batch.action.DeleteBatchSimulationAction
import org.pillarone.riskanalytics.application.ui.batch.action.OpenItemAction
import org.pillarone.riskanalytics.application.ui.batch.action.RunBatchSimulationAction
import org.pillarone.riskanalytics.core.BatchRun
import com.ulcjava.base.application.*

/**
 * @author fouad jaada
 */

public class BatchTableRenderer extends DefaultTableCellRenderer {
    BatchRun batchRun

    public IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean selected, boolean hasFocus, int column) {
        IRendererComponent component = super.getTableCellRendererComponent(table, value, selected, hasFocus, column)
        setHorizontalAlignment(component, value)
        component.setToolTipText String.valueOf(value)
        ULCPopupMenu nodePopup = new ULCPopupMenu()
        nodePopup.add(new ULCMenuItem(new RunBatchSimulationAction(model: table.model)))
        nodePopup.add(new ULCMenuItem(new ChangeBatchSimulationPriorityAction(table.model, 1)))
        nodePopup.add(new ULCMenuItem(new ChangeBatchSimulationPriorityAction(table.model, -1)))
        nodePopup.add(new ULCMenuItem(new DeleteBatchSimulationAction(model: table.model)))
        nodePopup.addSeparator()
        nodePopup.add(new ULCMenuItem(new OpenItemAction(table.model, OpenItemAction.SIMULATION, "BatchOpenSimulationAction")))
        nodePopup.add(new ULCMenuItem(new OpenItemAction(table.model, OpenItemAction.PARAMETERIZATION, "BatchOpenParameterizationAction")))
        nodePopup.add(new ULCMenuItem(new OpenItemAction(table.model, OpenItemAction.RESULT_CONFIG, "BatchOpenresultAction")))

        component.setComponentPopupMenu(nodePopup)
        return component
    }

    private void setHorizontalAlignment(IRendererComponent component, Number value) {
        component.horizontalAlignment = ULCLabel.RIGHT
    }

    private void setHorizontalAlignment(IRendererComponent component, def value) {
        component.horizontalAlignment = ULCLabel.LEFT
    }

}

class BatchTableHeaderRenderer extends DefaultTableHeaderCellRenderer {

    public IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean selected, boolean hasFocus, int column) {
        IRendererComponent component = super.getTableCellRendererComponent(table, value, selected, hasFocus, column)
        component.horizontalAlignment = ULCLabel.CENTER
        return component
    }
}
