package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.table.ULCTableColumn
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import java.awt.event.KeyEvent
import org.pillarone.riskanalytics.application.ui.base.action.TableCopier
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.chart.view.QueryPane
import org.pillarone.riskanalytics.application.ui.result.action.PercisionAction
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataViewModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.base.application.*

class ResultIterationDataView implements IModelChangedListener {
    ResultIterationDataViewModel model

    ULCBoxPane content
    ULCBoxPane criteriaButtonPane
    ULCBoxPane resultButtonPane
    ULCTable resultTable
    ULCLabel resultLabel

    ULCButton addCriteriaGroupButton
    ULCButton groupColumnsByPeriodButton
    ULCButton groupColumnsByPathButton
    ULCButton reducePrecisionButton
    ULCButton increasePrecisionButton
    ULCButton queryButton
    ULCButton exportButton

    ULCBoxPane queryPane

    private int defaultButtonWidth = 180
    private int defaultButtonHeigth = 25

    public ResultIterationDataView(ResultIterationDataViewModel model) {
        this.@model = model
        initComponents()
        layoutComponents()
        attachListeners()
    }

    void initComponents() {
        content = new ULCBoxPane(1, 4)
        content.name = "iterationDataView"

        criteriaButtonPane = new ULCBoxPane(0, 1)

        resultButtonPane = new ULCBoxPane(0, 1)

        addCriteriaGroupButton = new ULCButton(getText("addGroup"))
        groupColumnsByPeriodButton = new ULCButton(getText("columnOrderPeriod"))
        groupColumnsByPathButton = new ULCButton(getText("columnOrderKeyFigure"))
        reducePrecisionButton = new ULCButton(new PercisionAction(model, -1, "reducePrecision"))
        increasePrecisionButton = new ULCButton(new PercisionAction(model, +1, "increasePrecision"))

        queryButton = new ULCButton(getText("search"))
        exportButton = new ULCButton(new ExportRawDataTable(this))

        resultTable = new ULCTable(model.resultTableModel)
        resultTable.setAutoResizeMode(ULCTable.AUTO_RESIZE_OFF)
        resultTable.setCellSelectionEnabled true

        resultLabel = new ULCLabel(model.counterString)

        queryPane = new QueryPane(model).content
    }

    void layoutComponents() {
        content.removeAll()

        resultTable.columnModel.columnCount.times {int columnIndex ->
            ULCTableColumn column = resultTable.columnModel.getColumn(columnIndex)
            column.cellRenderer = model.resultTableModel.getCellRenderer(columnIndex)
            column.minWidth = 150
            if (columnIndex == 0) {
                column.minWidth = 75
                column.maxWidth = 75
                column.resizable = false
            }
            column.setHeaderRenderer(new IterationResultTableHeaderRenderer(model: model, columnIndex: columnIndex))
        }
        if (resultTable.columnModel.columnCount > 0) {
            resultTable.columnModel.getColumn(0).setCellRenderer(new IterationResultTableRenderer(model: model))
        }

        addCriteriaGroupButton.preferredSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)
        groupColumnsByPathButton.preferredSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)
        groupColumnsByPeriodButton.preferredSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)
        queryButton.preferredSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)
        exportButton.preferredSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)

        addCriteriaGroupButton.minimumSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)
        groupColumnsByPathButton.minimumSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)
        groupColumnsByPeriodButton.minimumSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)
        queryButton.minimumSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)
        exportButton.minimumSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)

        criteriaButtonPane.border = BorderFactory.createEmptyBorder(0, 6, 0, 0)
        criteriaButtonPane.add(ULCBoxPane.BOX_LEFT_TOP, addCriteriaGroupButton)
        criteriaButtonPane.add(ULCBoxPane.BOX_LEFT_TOP, queryButton)

        resultButtonPane.add(ULCBoxPane.BOX_LEFT_TOP, groupColumnsByPeriodButton)
        resultButtonPane.add(ULCBoxPane.BOX_LEFT_TOP, groupColumnsByPathButton)
        resultButtonPane.add(ULCBoxPane.BOX_LEFT_TOP, exportButton)
        resultButtonPane.add(ULCBoxPane.BOX_LEFT_TOP, reducePrecisionButton)
        resultButtonPane.add(ULCBoxPane.BOX_LEFT_TOP, increasePrecisionButton)

        ULCLabel topLabel = new ULCLabel(getText("searchLabel"))
        topLabel.border = BorderFactory.createEmptyBorder(5, 5, 0, 0)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, topLabel)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, queryPane)
        content.add(ULCBoxPane.BOX_LEFT_TOP, criteriaButtonPane)

        ULCBoxPane resultPane = new ULCBoxPane(1, 0)
        resultPane.border = BorderFactory.createTitledBorder(getText("results"))
        resultPane.add(ULCBoxPane.BOX_LEFT_TOP, resultButtonPane)
        resultPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(resultTable))
        resultPane.add(ULCBoxPane.BOX_LEFT_BOTTOM, resultLabel)

        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, resultPane)
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, legendPane)
    }

    void attachListeners() {
        addCriteriaGroupButton.addActionListener([actionPerformed: { model.addCriteriaGroup() }] as IActionListener)
        queryButton.addActionListener([actionPerformed: {
            model.validate() ? model.query() : new I18NAlert(UlcUtilities.getWindowAncestor(content), "PercentalNumberNotValid").show()
        }] as IActionListener)
        groupColumnsByPeriodButton.addActionListener([actionPerformed: { updateOrder(false)}] as IActionListener)
        groupColumnsByPathButton.addActionListener([actionPerformed: {updateOrder(true)}] as IActionListener)
        model.addModelChangedListener this
        resultTable.registerKeyboardAction(new TableCopier(table: resultTable), KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, false), 0)
    }

    protected updateOrder(boolean orderByPath) {
        model.orderByPath = orderByPath
        int columns = resultTable.getColumnCount()
        columns.times {int columnIndex ->
            resultTable.columnModel.getColumn(columnIndex).setHeaderValue(model.resultTableModel.getColumnName(columnIndex))
            resultTable.columnModel.getColumn(columnIndex).setHeaderRenderer(new IterationResultTableHeaderRenderer(model: model))
        }
        if (resultTable.columnModel.columnCount > 0) {
            resultTable.columnModel.getColumn(0).setCellRenderer(new IterationResultTableRenderer(model: model))
        }
    }


    public void modelChanged() {
        resultTable.setModel model.resultTableModel
        resultLabel.text = model.counterString
        layoutComponents()
    }

    private ULCBoxPane getLegendPane() {
        ULCBoxPane legendPane = new ULCBoxPane(3, 0)
        legendPane.border = BorderFactory.createTitledBorder(getText("legend"))
        model.displayPaths.eachWithIndex {String longPaths, int index ->
            legendPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.shortPaths[index]))
            legendPane.add(ULCFiller.createHorizontalStrut(20))
            legendPane.add(ULCBoxPane.BOX_EXPAND_CENTER, new ULCLabel(longPaths))
        }
        return legendPane
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ResultIterationDataView." + key);
    }

}




