package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import org.pillarone.riskanalytics.application.ui.chart.model.ParallelCoordinatesChartViewModel
import org.pillarone.riskanalytics.application.ui.chart.view.ChartView
import org.pillarone.riskanalytics.application.ui.chart.view.QueryPane
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.util.LocaleResources

class ParallelCoordinatesChartView extends ChartView {
    ULCButton addCriteriaGroupButton
    ULCButton queryButton
    ULCBoxPane criteriaButtonPane
    ULCSplitPane splitPane


    public ParallelCoordinatesChartView(ParallelCoordinatesChartViewModel model) {
        super(model)
    }

    void initComponents() {
        super.initComponents()
        criteriaButtonPane = new ULCBoxPane(0, 1)
        addCriteriaGroupButton = new ULCButton(getText("addGroup"))
        queryButton = new ULCButton(getText("redraw"))
    }

    void layoutComponents() {
        controlsPane.add(ULCBoxPane.BOX_EXPAND_TOP, legendPane)

        criteriaButtonPane.border = BorderFactory.createEmptyBorder(0, 6, 0, 0)
        criteriaButtonPane.add(ULCBoxPane.BOX_LEFT_TOP, addCriteriaGroupButton)
        criteriaButtonPane.add(ULCBoxPane.BOX_LEFT_TOP, queryButton)

        ULCBoxPane criteriaPane = new ULCBoxPane(1, 0)
        criteriaPane.add(ULCBoxPane.BOX_EXPAND_TOP, new QueryPane(model.queryPaneModel).content)
        criteriaPane.add(ULCBoxPane.BOX_LEFT_TOP, criteriaButtonPane)

        ULCBoxPane chartPane = new ULCBoxPane(1, 0)
        chartPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, ulcChart)
        chartPane.add(ULCBoxPane.BOX_EXPAND_BOTTOM, controlsPane)
        chartPane.add(ULCBoxPane.BOX_EXPAND_BOTTOM, periodPane)

        splitPane = new ULCSplitPane(ULCSplitPane.VERTICAL_SPLIT)
        splitPane.dividerSize = 10
        splitPane.oneTouchExpandable = true
        splitPane.topComponent = criteriaPane
        splitPane.bottomComponent = chartPane
        splitPane.resetToPreferredSizes()

        ULCLabel descLabel = new ULCLabel(getText("searchLabel")+":")
        descLabel.border = BorderFactory.createEmptyBorder(6, 2, 6, 0)
        content.add ULCBoxPane.BOX_LEFT_TOP, descLabel
        content.add ULCBoxPane.BOX_EXPAND_EXPAND, splitPane
    }

    private ULCBoxPane getLegendPane() {
        ULCBoxPane legendPane = new ULCBoxPane(3, 0)
        legendPane.border = BorderFactory.createTitledBorder(getText("legend"))
        model.seriesNames.eachWithIndex {String names, int index ->
            legendPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(names))
            ULCButton upButton = new ULCButton("<<")
            ULCButton downButton = new ULCButton(">>")
            upButton.addActionListener([actionPerformed: {e -> model.moveSeriesUp(index)}] as IActionListener)
            downButton.addActionListener([actionPerformed: {e -> model.moveSeriesDown(index)}] as IActionListener)
            upButton.enabled = index > 0
            downButton.enabled = index < (model.series.size() - 1)

            legendPane.add(ULCBoxPane.BOX_LEFT_CENTER, upButton)
            legendPane.add(ULCBoxPane.BOX_LEFT_CENTER, downButton)
        }
        return legendPane
    }

    public void modelChanged() {
        super.modelChanged()
        controlsPane.removeAll()
        controlsPane.add(ULCBoxPane.BOX_EXPAND_TOP, legendPane)
        splitPane?.resetToPreferredSizes()
    }

    void attachListeners() {
        super.attachListeners()
        queryButton.addActionListener([actionPerformed: { model.queryPaneModel.query() }] as IActionListener)
        addCriteriaGroupButton.addActionListener([actionPerformed: { model.queryPaneModel.addCriteriaGroup() }] as IActionListener)
    }

    protected ULCBoxPane getPeriodPane() {
        ULCButtonGroup periodButtonGroup = new ULCButtonGroup()
        ULCBoxPane periodBox = new ULCBoxPane(model.periodCount, 1)
        model.periodCount.times {int index ->
            ULCRadioButton radioButton = new ULCRadioButton(model.getPeriodLabel(index))
            radioButton.addValueChangedListener([valueChanged: {ValueChangedEvent event -> model.setPeriodVisibility(index, event.source.isSelected())}] as IValueChangedListener)
            radioButton.group = periodButtonGroup
            radioButton.name = "Period${index}RadioButton"
            if (index == 0) { radioButton.selected = true }
            periodBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, radioButton)
        }

        periodBox.border = BorderFactory.createTitledBorder("")
        return periodBox
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ParallelCoordinatesChartView." + key);
}}