package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.canoo.ulc.application.layout.ULCMigLayoutPane
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.table.ULCTableColumn
import java.awt.FontMetrics
import java.awt.image.BufferedImage
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterCompareViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterTableModel
import org.pillarone.riskanalytics.application.ui.table.view.MultiDimensionalParameterTableCellRenderer
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import com.ulcjava.base.application.table.ITableModel
import org.pillarone.riskanalytics.application.ui.table.view.MultiDimensionalParameterTableCompareRenderer

class MultiDimensionalParameterCompareView {

    MultiDimensionalParameterCompareViewModel model

    ULCMigLayoutPane content
    ULCTable referenceTable
    List<ULCTable> comparedTables = []

    MultiDimensionalParameterCompareView(MultiDimensionalParameterCompareViewModel model) {
        this.model = model
        initComponents()
        layoutComponents()
    }

    protected void initComponents() {
        content = new ULCMigLayoutPane("wrap ${model.comparedParameters.size() + 1}", "[]20[]", "[]20[]")

        final MultiDimensionalParameterTableModel tableModel = new MultiDimensionalParameterTableModel(model.referenceParameter, true)
        referenceTable = createTable(tableModel)
        referenceTable.name = "referenceTable"

        int i = 0
        for (AbstractMultiDimensionalParameter parameter in model.comparedParameters) {
            tableModel = new MultiDimensionalParameterTableModel(parameter, true)
            final ULCTable table = createComparingTable(tableModel, referenceTable.model)
            table.name = "table${i++}"
            comparedTables << table
        }
    }

    private int getColumnWidth(MultiDimensionalParameterTableModel tableModel, int column) {
        FontMetrics fontMetrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics().getFontMetrics()
        int sampleCount = Math.min(10, tableModel.multiDimensionalParam.getRowCount())
        int maxWidth = 0
        for (int i = 0; i < sampleCount; i++) {
            def values = tableModel.getPossibleValues(i, column)

            if (values instanceof List) {
                for (value in values) {
                    maxWidth = Math.max(maxWidth, fontMetrics.stringWidth(value.toString()) + 10)
                }
            } else {
                maxWidth = Math.max(maxWidth, fontMetrics.stringWidth(values.toString()) + 10)
            }
        }
        return maxWidth
    }

    protected ULCTable createTable(MultiDimensionalParameterTableModel tableModel) {
        tableModel.readOnly = true
        final ULCTable table = new ULCTable(tableModel)
        table.setTableHeader(null)
        table.cellSelectionEnabled = true
        table.setAutoResizeMode(ULCTable.AUTO_RESIZE_OFF)

        table.getColumnModel().getColumns().eachWithIndex {ULCTableColumn column, int index ->
            column.cellRenderer = new MultiDimensionalParameterTableCellRenderer(column: index)
            if (index > 0) {
                column.minWidth = getColumnWidth(tableModel, index)
                column.resizable = true
            } else {
                column.minWidth = 50
            }
        }

        return table
    }

    protected ULCTable createComparingTable(MultiDimensionalParameterTableModel tableModel, ITableModel referenceModel) {
        tableModel.readOnly = true
        final ULCTable table = new ULCTable(tableModel)
        table.setTableHeader(null)
        table.cellSelectionEnabled = true
        table.setAutoResizeMode(ULCTable.AUTO_RESIZE_OFF)

        table.getColumnModel().getColumns().eachWithIndex {ULCTableColumn column, int index ->
            column.cellRenderer = new MultiDimensionalParameterTableCompareRenderer(index, referenceModel)
            if (index > 0) {
                column.minWidth = getColumnWidth(tableModel, index)
                column.resizable = true
            } else {
                column.minWidth = 50
            }
        }

        return table
    }

    protected void layoutComponents() {

        for (Parameterization parameterization in model.parameterizations) {
            content.add(new ULCLabel(parameterization.toString() + " " + parameterization.getPeriodLabel(model.periodIndex)))
        }

        final ULCScrollPane scrollPane = new ULCScrollPane(referenceTable)
        scrollPane.setHorizontalScrollBarPolicy(ULCScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        scrollPane.setVerticalScrollBarPolicy(ULCScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED)
        content.add(scrollPane)

        for (ULCTable table in comparedTables) {
            scrollPane = new ULCScrollPane(table)
            scrollPane.setHorizontalScrollBarPolicy(ULCScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
            scrollPane.setVerticalScrollBarPolicy(ULCScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED)
            content.add(scrollPane)
        }
    }

}
