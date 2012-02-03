package org.pillarone.riskanalytics.application.ui.table.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.table.ITableModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationUtilities


class MultiDimensionalParameterTableCompareRenderer extends MultiDimensionalParameterTableCellRenderer {

    ITableModel referenceTableModel

    MultiDimensionalParameterTableCompareRenderer(int column, ITableModel referenceTableModel) {
        this.column = column
        this.referenceTableModel = referenceTableModel
    }

    @Override
    IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row) {
        final IRendererComponent component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row)
        if (table.model.getValueAt(row, column) != referenceTableModel.getValueAt(row, column)) {
            background = ParameterizationUtilities.ERROR_BG
        }
        return component
    }
}
