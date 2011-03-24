package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCSlider
import com.ulcjava.base.application.event.IValueChangedListener
import org.pillarone.riskanalytics.application.ui.chart.model.HistogramChartViewModel
import org.pillarone.riskanalytics.application.util.LocaleResources

class HistogramChartView extends ChartView {
    ULCSlider binSlider
    ULCLabel currentBinCountLabel

    public HistogramChartView() {}

    public HistogramChartView(HistogramChartViewModel model) {
        super(model)
    }

    protected void initComponents() {
        super.initComponents()

        int currentValue = model.currentBinCount == -1 ? model.minBinCount : model.currentBinCount

        binSlider = new ULCSlider(ULCSlider.HORIZONTAL, model.minBinCount, model.maxBinCount, currentValue)
        binSlider.minorTickSpacing = 5
        binSlider.majorTickSpacing = 20
        binSlider.paintTicks = true
        binSlider.snapToTicks = false
        binSlider.paintLabels = true


        currentBinCountLabel = new ULCLabel("${getText("currentBinCount")}: ${model.currentBinCount}")
    }

    void layoutComponents() {

        ULCBoxPane binSliderBox = new ULCBoxPane(3, 2)
        binSliderBox.add(ULCBoxPane.BOX_RIGHT_EXPAND, new ULCLabel("" + model.minBinCount))
        binSliderBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, binSlider)
        binSliderBox.add(ULCBoxPane.BOX_LEFT_EXPAND, new ULCLabel("" + model.maxBinCount))
        binSliderBox.add(3, ULCBoxPane.BOX_CENTER_EXPAND, currentBinCountLabel)

        binSliderBox.border = BorderFactory.createTitledBorder("${getText("bins")}:")

        controlsPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, binSliderBox)
        super.layoutComponents()
    }

    void attachListeners() {
        super.attachListeners()
        binSlider.addValueChangedListener([valueChanged: {event -> model.currentBinCount = binSlider.getValue()}] as IValueChangedListener)
    }

    public void modelChanged() {
        super.modelChanged()
        currentBinCountLabel.text = "${getText("currentBinCount")}: " + binSlider.getValue()
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("HistogramChartView." + key);
    }
}