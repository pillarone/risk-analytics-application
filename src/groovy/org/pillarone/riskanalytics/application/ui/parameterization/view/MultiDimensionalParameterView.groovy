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
import org.pillarone.riskanalytics.application.ui.table.view.MultiDimensionalParameterTableCellRenderer
import org.pillarone.riskanalytics.application.ui.table.view.MultiDimensionalTable
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.parameterization.MultiDimensionalParameterDimension
import com.ulcjava.base.application.*

class MultiDimensionalParameterView extends AbstractMultiDimensionalParameterView {
    ULCSpinner rowCount
    ULCSpinner columnCount
    ULCButton applyDimensionButton
    ULCButton addRowButton
    ULCLabel path

    MultiDimensionalParameterView(MultiDimensionalParameterModel model) {
        super(model)
    }

    protected void initComponents() {
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
        addRowButton = new ULCButton(getText("addRow"))
        addRowButton.name = "addRowButton"
        applyDimensionButton.enabled = (columnCount.enabled || rowCount.enabled) && !model.tableModel.readOnly
        addRowButton.enabled = (columnCount.enabled || rowCount.enabled) && !model.tableModel.readOnly

        setRendererAndEditors()
        //set table header height
        this.table.getTableHeader().setPreferredSize(new Dimension(80, 5))
        this.table.cellSelectionEnabled = true

        rowCount.enabled = !model.tableModel.readOnly
        columnCount.enabled = !model.tableModel.readOnly
    }

    protected void layoutComponents() {
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
            dimensionSection = new ULCBoxPane(4, 1)
            dimensionSection.add(new ULCLabel(getText("dimension")))
            dimensionSection.add(columnCount)
            dimensionSection.add(applyDimensionButton)
            dimensionSection.add(addRowButton)
        } else if (model.tableModel.columnCountChangeable()) {
            dimensionSection = new ULCBoxPane(4, 2)
            dimensionSection.add(new ULCLabel(getText("rowCount")))
            dimensionSection.add(rowCount)
            dimensionSection.add(2, new ULCFiller())
            dimensionSection.add(new ULCLabel(getText("columnCount")))
            dimensionSection.add(columnCount)
            dimensionSection.add(applyDimensionButton)
            dimensionSection.add(addRowButton)
        } else {
            dimensionSection = new ULCBoxPane(4, 1)
            dimensionSection.add(new ULCLabel(getText("rowCount")))
            dimensionSection.add(rowCount)
            dimensionSection.add(applyDimensionButton)
            dimensionSection.add(addRowButton)
        }
        return dimensionSection
    }

   protected void attachListeners() {
        applyDimensionButton.addActionListener([actionPerformed: {
            if (isMatrix())
                rowCount.value = columnCount.value
            MultiDimensionalParameterDimension dimension = new MultiDimensionalParameterDimension(columnCount.value, rowCount.value)
            model.tableModel.dimension = dimension
        }] as IActionListener)
        addRowButton.addActionListener([actionPerformed: {
            rowCount.value = rowCount.value + 1
            if (isMatrix()) {
                columnCount.value = columnCount.value + 1
            }

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