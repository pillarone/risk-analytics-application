package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory

class MultiDimensionalParameterTableCellRenderer extends DefaultTableCellRenderer {

    int column

    protected String typeString() {
        return "org.pillarone.riskanalytics.application.client.UIErrorFeedbackLabel"
    }

    public IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row) {
        setBackgroundColor(table, row)
        IRendererComponent rendererComponent = createRenderer(table, value, isSelected, hasFocus, row, table.model.getPossibleValues(row, column))
        if (value) {
            rendererComponent.setToolTipText(value.toString())
        }
        return rendererComponent
    }

    private IRendererComponent createRenderer(ULCTable table, def value, boolean isSelected, boolean hasFocus, int row, def values) {
        IRendererComponent rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row)
        rendererComponent.dataType = DataTypeFactory.getDataType(value, false)
        return rendererComponent
    }

    private IRendererComponent createRenderer(ULCTable table, def value, boolean isSelected, boolean hasFocus, int row, List values) {
        return createComboBox(values)
    }

    private ULCComboBox createComboBox(List values) {
        return new ULCComboBox(values)
    }

    private void setBackgroundColor(ULCTable table, int row) {
        boolean isTitleRow = !table.getModel().isCellEditable(row, column)
        if (isTitleRow) {
            setBackground(Color.lightGray)
        } else {
            setBackground(Color.white)
        }
    }


}