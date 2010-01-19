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
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.ui.base.action.TableCopier
import org.pillarone.riskanalytics.application.ui.base.action.TablePaster
import org.pillarone.riskanalytics.application.ui.base.action.TableSelectionFiller
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalParameterTableCellEditor
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalParameterTableCellRenderer
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
        table = new ULCTable(model.getTableModel())
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
        spinnerModel.value = model.tableModel.valueColumnCount
        columnCount = new ULCSpinner(spinnerModel)
        columnCount.preferredSize = new Dimension(80, 20)
        columnCount.name = 'columnCount'
        columnCount.enabled = model.tableModel.columnCountChangeable()
        applyDimensionButton = new ULCButton(getText("apply"))
        applyDimensionButton.enabled = columnCount.enabled || rowCount.enabled

        setRendererAndEditors()
        table.tableHeader = null
        table.cellSelectionEnabled = true
    }

    private def setRendererAndEditors() {
        table.getColumnModel().getColumns().eachWithIndex {ULCTableColumn column, int index ->
            column.minWidth = getColumnWidth(index)
            column.setCellRenderer(new MultiDimensionalParameterTableCellRenderer(column: index))
            column.setCellEditor(new MultiDimensionalParameterTableCellEditor(column: index))
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
        ULCBoxPane dimensionSection = new ULCBoxPane(3, 2)
        dimensionSection.add(new ULCLabel(getText("rowCount")))
        dimensionSection.add(rowCount)
        dimensionSection.add(new ULCFiller())
        dimensionSection.add(new ULCLabel(getText("columnCount")))
        dimensionSection.add(columnCount)
        dimensionSection.add(applyDimensionButton)
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

    void attachListeners() {
        applyDimensionButton.addActionListener([actionPerformed: {
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
        return LocaleResources.getString("MultiDimensionalParameterView." + key);
    }

}