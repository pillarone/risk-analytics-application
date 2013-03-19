package org.pillarone.riskanalytics.application.ui.table.view

import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.IRendererComponent
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.ULCComboBox
import org.pillarone.riskanalytics.application.ui.parameterization.view.TableTreeComboBoxModel
import com.ulcjava.base.application.table.DefaultTableHeaderCellRenderer
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.datatype.IDataType
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterTableModel
import org.joda.time.DateTime

class MultiDimensionalParameterTableCellRenderer extends DefaultTableCellRenderer {

    int column

    private DefaultTableHeaderCellRenderer template = new DefaultTableHeaderCellRenderer()

    @Override
    IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row) {
        IRendererComponent component = createRendererComponent(table, value, isSelected, hasFocus, row, getTableModel(table).getPossibleValues(row, column))
        AbstractMultiDimensionalParameter parameter = getMultiDimensionalParameter(table)
        if (isHeaderCell(parameter, row)) {
            configureHeaderRenderer(component, value)
        } else {
            configureCellRenderer(component, parameter, value)
            if(!isSelected) {
                component.background = Color.white
            }
        }
        if (table instanceof MultiDimensionalTable) {
            component.componentPopupMenu = ((MultiDimensionalTable) table).getPopupMenu(row, column)
        }
        return component
    }

    //create renderer components

    protected IRendererComponent createRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row, def possibleValues) {
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row)
    }

    protected IRendererComponent createRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row, List possibleValues) {
        return new ULCComboBox(new TableTreeComboBoxModel(possibleValues))
    }

    //configure normal cells

    protected void configureCellRenderer(ULCLabel label, AbstractMultiDimensionalParameter parameter, def value) {
        label.horizontalAlignment = ULCLabel.RIGHT
        label.font = label.font.deriveFont(Font.PLAIN)
        label.dataType = getDataType(parameter, value)
        label.toolTipText = value.toString()
    }

    protected void configureCellRenderer(def component, AbstractMultiDimensionalParameter parameter, def value) {
    }

    //obtain datatypes

    protected IDataType getDataType(AbstractMultiDimensionalParameter parameter, def value) {
        return DataTypeFactory.getDataType(value, false)
    }

    protected IDataType getDataType(ConstrainedMultiDimensionalParameter parameter, def value) {
        Class columnType = parameter.constraints.getColumnType(column - 1)
        switch (columnType) {
            case Integer:
                return DataTypeFactory.getIntegerDataTypeForNonEdit()
            case Double:
                return DataTypeFactory.getDoubleDataTypeForNonEdit()
            case Date:
                return DataTypeFactory.getDateDataType()
            case DateTime:
                return DataTypeFactory.getDateDataType()
        }
        return null
    }

    //configure header cells

    protected void configureHeaderRenderer(ULCLabel label, def value) {
        label.horizontalAlignment = ULCLabel.CENTER
        label.background = template.background
        label.foreground = template.foreground
        label.font = template.font
        label.border = template.border
        label.toolTipText = value.toString()
    }

    protected void configureHeaderRenderer(def component, def value) {
    }

    protected boolean isHeaderCell(AbstractMultiDimensionalParameter parameter, int row) {
        return column < parameter.titleColumnCount + 1 || row < parameter.titleRowCount
    }

    protected AbstractMultiDimensionalParameter getMultiDimensionalParameter(ULCTable table) {
        return getTableModel(table).multiDimensionalParam
    }

    protected MultiDimensionalParameterTableModel getTableModel(ULCTable table) {
        return table.model
    }

    protected String typeString() {
        return "org.pillarone.ulc.client.UIErrorFeedbackLabel"
    }

}
