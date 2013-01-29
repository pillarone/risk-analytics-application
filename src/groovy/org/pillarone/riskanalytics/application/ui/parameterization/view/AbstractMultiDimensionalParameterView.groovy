package org.pillarone.riskanalytics.application.ui.parameterization.view

import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.table.ULCTableColumn
import org.pillarone.riskanalytics.application.ui.table.view.MultiDimensionalParameterTableCellRenderer
import java.awt.FontMetrics
import java.awt.image.BufferedImage
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

abstract class AbstractMultiDimensionalParameterView {
    MultiDimensionalParameterModel model
    ULCTable table
    ULCBoxPane content

    AbstractMultiDimensionalParameterView(MultiDimensionalParameterModel model) {
        this.model = model
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected abstract void initComponents()

    protected abstract void layoutComponents()

    protected abstract void attachListeners()

    public abstract boolean isMatrix()

    public abstract void updateCount(boolean isRow, int x)

    protected void setRendererAndEditors() {
        table.getColumnModel().getColumns().eachWithIndex {ULCTableColumn column, int index ->
            if (index == 0) {
                column.minWidth = 50
                column.maxWidth = 50
            } else {
                column.minWidth = getColumnWidth(index)
            }
            column.setCellRenderer(new MultiDimensionalParameterTableCellRenderer(column: index))
            column.setCellEditor(new MultiDimensionalParameterTableCellEditor(column: index))
            column.resizable = true
            column.setHeaderValue("")
        }
    }


    protected int getColumnWidth(int column) {
        FontMetrics fontMetrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics().getFontMetrics()
        int sampleCount = Math.min(10, model.multiDimensionalParameter.getRowCount())

        int maxWidth = 0
        for (int i = 0; i < sampleCount; i++) {
            def values = model.tableModel.getPossibleValues(i, column)

            if (values instanceof List) {
                for (value in values) {
                    maxWidth = Math.max(maxWidth, fontMetrics.stringWidth(value.toString()) + 10)
                }
            } else {
                maxWidth = Math.max(maxWidth, fontMetrics.stringWidth(values.toString()))
            }
        }
        return maxWidth
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return UIUtils.getText(getClass(), key)
    }

}
