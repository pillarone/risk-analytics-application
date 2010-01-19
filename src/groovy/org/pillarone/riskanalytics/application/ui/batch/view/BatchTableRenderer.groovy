package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.table.DefaultTableHeaderCellRenderer
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCMenuItem
import org.pillarone.riskanalytics.application.ui.batch.action.RunBatchSimulationAction
import org.pillarone.riskanalytics.core.BatchRun

import org.pillarone.riskanalytics.application.ui.batch.action.DeleteBatchSimulationAction
import org.pillarone.riskanalytics.application.ui.batch.action.ChangeBatchSimulationPriorityAction

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
        //todo deactivate edit at the moment
//        nodePopup.add(new ULCMenuItem(new EditBatchSimulationAction(model: table.model)))
        nodePopup.add(new ULCMenuItem(new ChangeBatchSimulationPriorityAction(table.model,1)))
        nodePopup.add(new ULCMenuItem(new ChangeBatchSimulationPriorityAction(table.model,-1)))
        nodePopup.add(new ULCMenuItem(new DeleteBatchSimulationAction(model: table.model)))
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
