package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ITableModelListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.event.TableModelEvent
import com.ulcjava.base.application.table.ULCTableColumn
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import java.awt.FontMetrics
import java.awt.image.BufferedImage
import org.pillarone.riskanalytics.application.ui.base.action.TableCopier
import org.pillarone.riskanalytics.application.ui.base.action.TablePaster
import org.pillarone.riskanalytics.application.ui.base.action.TableSelectionFiller
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.table.MultiDimensionalTableCellRenderer
import org.pillarone.riskanalytics.application.ui.table.view.MultiDimensionalTable
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.parameterization.MultiDimensionalParameterDimension
import com.ulcjava.base.application.*

class MultiDimensionalParameterView {
    ULCBoxPane content
    ULCTable table
    MultiDimensionalParameterModel model
    ULCSpinner rowCount
    ULCSpinner columnCount
    ULCButton applyDimensionButton
    ULCLabel path

    MultiDimensionalParameterView(MultiDimensionalParameterModel model) {
        this.model = model
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        path = new ULCLabel(model.getPathAsString())
        content = new ULCBoxPane(1, 0)
        this.table = new MultiDimensionalTable(this, model.getTableModel())
        ULCSpinnerNumberModel spinnerModel = new ULCSpinnerNumberModel()
        spinnerModel.minimum = model.multiDimensionalParameter.supportsZeroRows() ? 0 : 1
        spinnerModel.stepSize = 1
        spinnerModel.value = model.tableModel.valueRowCount
        rowCount = new ULCSpinner(spinnerModel)
        rowCount.name = 'rowCount'
        rowCount.preferredSize = new Dimension(80, 20)
        rowCount.enabled = model.tableModel.rowCountChangeable()
        spinnerModel = new ULCSpinnerNumberModel()
        spinnerModel.minimum = 1
        spinnerModel.stepSize = 1
        //-1 for index column
        spinnerModel.value = model.tableModel.valueColumnCount - 1
        columnCount = new ULCSpinner(spinnerModel)
        columnCount.preferredSize = new Dimension(80, 20)
        columnCount.name = 'columnCount'
        columnCount.enabled = model.tableModel.columnCountChangeable()
        applyDimensionButton = new ULCButton(getText("apply"))
        applyDimensionButton.enabled = columnCount.enabled || rowCount.enabled

        setRendererAndEditors()
        this.table.getTableHeader().setPreferredSize(new Dimension(80, 5))
        this.table.cellSelectionEnabled = true
    }

    private def setRendererAndEditors() {
        table.getColumnModel().getColumns().eachWithIndex {ULCTableColumn column, int index ->
            column.minWidth = getColumnWidth(index)
            if (index == 0) {
                column.minWidth = 50
                column.maxWidth = 50
            }
            column.setCellRenderer(new MultiDimensionalTableCellRenderer(index, table))
            column.setCellEditor(new MultiDimensionalParameterTableCellEditor(column: index))
            column.resizable = true
            column.setHeaderValue("")
        }
    }

    private int getColumnWidth(int column) {
        FontMetrics fontMetrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics().getFontMetrics()
        int sampleCount = Math.max(10, model.multiDimensionalParameter.getRowCount())
        int maxWidth = 0
        for (int i = 0; i < sampleCount; i++) {
            def values = model.multiDimensionalParameter.getPossibleValues(i, column)

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

    private void layoutComponents() {
        ULCBoxPane dimensionSection = getDimensionSection()
        content.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller(10, 10))
        content.add(ULCBoxPane.BOX_EXPAND_TOP, path)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller(10, 10))
        content.add(ULCBoxPane.BOX_LEFT_CENTER, dimensionSection)

        table.setAutoResizeMode(ULCTable.AUTO_RESIZE_OFF)
        ULCScrollPane scrollPane = new ULCScrollPane(table)
        scrollPane.setHorizontalScrollBarPolicy(ULCScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        scrollPane.setVerticalScrollBarPolicy(ULCScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, scrollPane)
    }

    private ULCBoxPane getDimensionSection() {
        ULCBoxPane dimensionSection
        if (isMatrix()) {
            dimensionSection = new ULCBoxPane(3, 1)
            dimensionSection.add(new ULCLabel(getText("dimension")))
            dimensionSection.add(columnCount)
            dimensionSection.add(applyDimensionButton)
        } else if (model.tableModel.columnCountChangeable()) {
            dimensionSection = new ULCBoxPane(3, 2)
            dimensionSection.add(new ULCLabel(getText("rowCount")))
            dimensionSection.add(rowCount)
            dimensionSection.add(new ULCFiller())
            dimensionSection.add(new ULCLabel(getText("columnCount")))
            dimensionSection.add(columnCount)
            dimensionSection.add(applyDimensionButton)
        } else {
            dimensionSection = new ULCBoxPane(3, 1)
            dimensionSection.add(new ULCLabel(getText("rowCount")))
            dimensionSection.add(rowCount)
            dimensionSection.add(applyDimensionButton)
        }
        return dimensionSection
    }

    void attachListeners() {
        applyDimensionButton.addActionListener([actionPerformed: {
            if (isMatrix())
                rowCount.value = columnCount.value
            MultiDimensionalParameterDimension dimension = new MultiDimensionalParameterDimension(columnCount.value, rowCount.value)
            model.tableModel.dimension = dimension
        }] as IActionListener)
        model.tableModel.addTableModelListener([tableChanged: {TableModelEvent event ->
            if (event.firstRow == -1 || event.column == -1) {
                setRendererAndEditors()
            }
        }
        ] as ITableModelListener)
        table.registerKeyboardAction(new TableCopier(table: table), KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, false), 0)
        table.registerKeyboardAction(new TablePaster(table: table, model: table.model), KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK, false), 0)
        table.registerKeyboardAction(new TableSelectionFiller(table: table, model: table.model), KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK, false), 0)
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return UIUtils.getText(this.class, key)
    }

    public void updateCount(boolean isRow, int x) {
        //-1 for index column
        if (isRow)
            rowCount.value = rowCount.value + x
        else
            columnCount.value = columnCount.value + x
    }

    public boolean isMatrix() {
        return model.isMatrix()
    }


}