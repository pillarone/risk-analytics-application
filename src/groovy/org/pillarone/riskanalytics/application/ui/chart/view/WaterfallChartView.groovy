package org.pillarone.riskanalytics.application.ui.chart.view

import com.canoo.ulc.community.jfreechart.server.ULCJFreeChart
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.ui.chart.action.ChartDataExportAction
import org.pillarone.riskanalytics.application.ui.chart.action.ChartPictureExportAction
import org.pillarone.riskanalytics.application.ui.chart.model.WaterfallChartViewModel
import org.pillarone.riskanalytics.application.ui.chart.view.ChartView
import org.pillarone.riskanalytics.application.ui.chart.view.OpenChartPropertiesAction
import org.pillarone.riskanalytics.application.ui.chart.view.ResetZoomAction
import com.ulcjava.base.application.*


public class WaterfallChartView extends ChartView {
    ULCSpinner percentSpinner
    ULCRadioButton VaR
    ULCRadioButton TVaR
    ULCButton applyButton

    public WaterfallChartView(WaterfallChartViewModel model) {
        super(model)
    }


    void initComponents() {
        ulcChart = new ULCJFreeChart()
        ULCPopupMenu resultNodePopup = new ULCPopupMenu()
        resultNodePopup.add(new ULCMenuItem(new ChartPictureExportAction(this)))
        resultNodePopup.add(new ULCMenuItem(new ChartDataExportAction(this)))
        resultNodePopup.addSeparator()
        resultNodePopup.add(new ULCMenuItem(new ResetZoomAction(this)))
        resultNodePopup.addSeparator()
        resultNodePopup.add(new ULCMenuItem(new OpenChartPropertiesAction(this, [true, true, true, false])))
        ulcChart.setComponentPopupMenu(resultNodePopup)
        controlsPane = new ULCBoxPane(1, 0)

        percentSpinner = new ULCSpinner(new ULCSpinnerNumberModel(99.5, 0, 100, 0.5))
        VaR = new ULCRadioButton(getText("VaR"))
        TVaR = new ULCRadioButton(getText("TVaR"))
        applyButton = new ULCButton(getText("apply"))
        applyButton.enabled = false
    }

    void layoutComponents() {
        controlsPane.add ULCBoxPane.BOX_EXPAND_BOTTOM, legendPane

        content.add ULCBoxPane.BOX_EXPAND_EXPAND, ulcChart
        content.add ULCBoxPane.BOX_EXPAND_BOTTOM, controlsPane
        content.add ULCBoxPane.BOX_EXPAND_BOTTOM, periodPane
        content.add ULCBoxPane.BOX_EXPAND_BOTTOM, functionPane
    }

    private ULCBoxPane getFunctionPane() {
        ULCBoxPane functionPane = new ULCBoxPane(4, 0)
        functionPane.border = BorderFactory.createTitledBorder(getText("parameters"))
        percentSpinner.preferredSize = new Dimension(100, 21)
        functionPane.add(ULCBoxPane.BOX_CENTER_CENTER, percentSpinner)
        ULCButtonGroup functionButtonGroup = new ULCButtonGroup()
        VaR.group = functionButtonGroup
        TVaR.group = functionButtonGroup
        VaR.selected = true
        functionPane.add(ULCBoxPane.BOX_CENTER_CENTER, VaR)
        functionPane.add(ULCBoxPane.BOX_CENTER_CENTER, TVaR)
        functionPane.add(ULCBoxPane.BOX_CENTER_CENTER, applyButton)
        return functionPane
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
            downButton.enabled = index < (model.seriesNames.size() - 1)

            legendPane.add(ULCBoxPane.BOX_LEFT_CENTER, upButton)
            legendPane.add(ULCBoxPane.BOX_LEFT_CENTER, downButton)
        }
        return legendPane
    }

    public void modelChanged() {
        super.modelChanged()
        controlsPane.removeAll()
        controlsPane.add(ULCBoxPane.BOX_EXPAND_TOP, legendPane)
    }

    void attachListeners() {
        super.attachListeners()
        percentSpinner.addValueChangedListener([valueChanged: {event -> applyButton.enabled = true }] as IValueChangedListener)
        VaR.addValueChangedListener([valueChanged: {event -> applyButton.enabled = true }] as IValueChangedListener)
        TVaR.addValueChangedListener([valueChanged: {event -> applyButton.enabled = true }] as IValueChangedListener)
        applyButton.addActionListener([actionPerformed: {event ->
            if (VaR.selected) {
                model.setFunction(WaterfallChartViewModel.VAR_FUNCTION, percentSpinner.value)
            } else {
                model.setFunction(WaterfallChartViewModel.TVAR_FUNCTION, percentSpinner.value)
            }
            applyButton.enabled = false
        }] as IActionListener)
    }

    protected ULCBoxPane getPeriodPane() {
        ULCButtonGroup periodButtonGroup = new ULCButtonGroup()
        ULCBoxPane periodBox = new ULCBoxPane(model.periodCount, 1)
        model.periodCount.times {int index ->
            ULCRadioButton radioButton = new ULCRadioButton("${model.getPeriodLabel(index)}")
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
        return LocaleResources.getString("WaterfallChartView." + key);
    }

}