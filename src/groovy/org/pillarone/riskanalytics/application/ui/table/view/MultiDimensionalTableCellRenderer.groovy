package org.pillarone.riskanalytics.application.ui.table

import com.ulcjava.base.application.border.ULCEmptyBorder
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.table.DefaultTableHeaderCellRenderer
import com.ulcjava.base.application.table.ITableCellRenderer
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MultiDimensionalTableCellRenderer extends DefaultTableCellRenderer {

    private int column
    ULCTable table
    DefaultTableHeaderCellRenderer headerRenderer
    ITableCellRenderer cellRenderer
    ULCEmptyBorder emptyBorder

    protected String typeString() {
        return "org.pillarone.riskanalytics.application.client.UIErrorFeedbackLabel"
    }

    public MultiDimensionalTableCellRenderer(int column, ULCTable table) {
        this.@column = column
        this.@table = table
        headerRenderer = new DefaultTableHeaderCellRenderer()
        cellRenderer = new DefaultTableCellRenderer()
        emptyBorder = new MultiDimensionalTableHeaderEmptyBorder()
    }

    public IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row) {
        ITableCellRenderer renderer = getRenderer(table, row, column, isSelected)
        renderer.setComponentPopupMenu(getPopupMenu(row, column))
        IRendererComponent rendererComponent = createRenderer(renderer, table, value, isSelected, hasFocus, row, table.model.getPossibleValues(row, column))
        if (!(rendererComponent instanceof ULCComboBox))
            rendererComponent?.dataType = DataTypeFactory.getDataType(value, false)
        if (!isHeaderCell(table, row, column) && !hasFocus) {
            rendererComponent.border = emptyBorder
            setBackground(Color.white)
        }
        if (value) {
            rendererComponent.setToolTipText(value.toString())
        }

        return rendererComponent
    }

    private ITableCellRenderer getRenderer(ULCTable table, int row, int column, boolean isSelected) {
        if (isHeaderCell(table, row, column)) {
            headerRenderer.setHorizontalAlignment(ULCLabel.LEFT)
            return headerRenderer
        }
        cellRenderer.setBackground(Color.white)
        return cellRenderer
    }

    private IRendererComponent createRenderer(def renderer, ULCTable table, def value, boolean isSelected, boolean hasFocus, int row, def values) {
        IRendererComponent rendererComponent = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row)
        if (!(rendererComponent instanceof ULCComboBox)) {
            rendererComponent?.dataType = DataTypeFactory.getDataType(value, false)
            rendererComponent.horizontalAlignment = (!(value instanceof Number) || column == 0) ? ULCLabel.CENTER : ULCLabel.RIGHT
        }

        return rendererComponent
    }

    private IRendererComponent createRenderer(def renderer, ULCTable table, def value, boolean isSelected, boolean hasFocus, int row, List values) {
        return createComboBox(values)
    }

    private ULCComboBox createComboBox(List values) {
        return new ULCComboBox(values)
    }


    private boolean isHeaderCell(ULCTable table, int row, int column) {
        return row == 0 || column == 0
    }


    public ULCPopupMenu getPopupMenu(int row, int column) {
        return table.getPopupMenu(row, column)
    }

}

class MultiDimensionalTableHeaderEmptyBorder extends ULCEmptyBorder {

    public MultiDimensionalTableHeaderEmptyBorder() {
        super(1, 1, 1, 1)
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ULCEmptyBorder)) {
            return false
        }
        return borderInsets.equals(obj.borderInsets)
    }

    public int hashCode() {
        return borderInsets.hashCode()
    }

}
