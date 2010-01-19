package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCSlider
import com.ulcjava.base.application.event.IValueChangedListener
import org.pillarone.riskanalytics.application.ui.chart.model.DiscretePDFChartViewModel
import org.pillarone.riskanalytics.application.ui.chart.view.ChartView

class DiscretePDFChartView extends ChartView {
    ULCBoxPane controlsPane
    ULCSlider binSlider
    ULCLabel currentBinSizeLabel

    public DiscretePDFChartView() {}

    public DiscretePDFChartView(DiscretePDFChartViewModel model) {
        super(model)
        attachListeners()
    }

    protected void initComponents() {
        super.initComponents()

        controlsPane = new ULCBoxPane()
        binSlider = new ULCSlider(ULCSlider.HORIZONTAL, 1, model.maxBinSize, model.currentBinSize)
        binSlider.paintTicks = true
        binSlider.paintLabels = true

        currentBinSizeLabel = new ULCLabel("current bin size: ")

        ULCBoxPane binSliderBox = new ULCBoxPane(3, 2)
        binSliderBox.add(ULCBoxPane.BOX_RIGHT_EXPAND, new ULCLabel("1"))
        binSliderBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, binSlider)
        binSliderBox.add(ULCBoxPane.BOX_LEFT_EXPAND, new ULCLabel("" + model.maxBinSize))
        binSliderBox.add(3, ULCBoxPane.BOX_CENTER_EXPAND, currentBinSizeLabel)

        binSliderBox.border = BorderFactory.createTitledBorder("bin size:")

        controlsPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, binSliderBox)
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, controlsPane)
    }


    void attachListeners() {
        binSlider.addValueChangedListener([valueChanged: {event -> model.currentBinSize = binSlider.getValue()}] as IValueChangedListener)
    }

    public void modelChanged() {
        super.modelChanged()
        currentBinSizeLabel.text = "current bin size: " + binSlider.getValue()
    }
}