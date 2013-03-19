package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.table.DefaultTableHeaderCellRenderer
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataViewModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class IterationResultTableHeaderRenderer extends DefaultTableHeaderCellRenderer {
    ResultIterationDataViewModel model
    int columnIndex

    public IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean selected, boolean hasFocus, int column) {
        IRendererComponent component = super.getTableCellRendererComponent(table, value, selected, hasFocus, column)
        if (!value.equals("Iteration")) {
            component.setToolTipText(model.resultTableModel.getColumnName(columnIndex))
        }
        return component
    }

}
