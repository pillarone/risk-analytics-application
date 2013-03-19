package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import java.text.DecimalFormat
import org.pillarone.riskanalytics.application.ui.chart.model.KernelEstimatorChartViewModel
import org.pillarone.riskanalytics.application.ui.chart.view.ChartView
import com.ulcjava.base.application.*

class PDFChartView extends ChartView {
    ULCSpinner bandwidthSpinner
    ULCButton applyButton
    ULCBoxPane parametersBox

    public PDFChartView(KernelEstimatorChartViewModel model) {
        super(model)
    }

    protected void initComponents() {
        super.initComponents()
        parametersBox = new ULCBoxPane(4, 0)
        bandwidthSpinner = new ULCSpinner(model.getSpinnerModel())
        applyButton = new ULCButton("apply")
        applyButton.enabled = false
    }

    void layoutComponents() {
        if (model.periodCount != 1 || model.seriesNames.size() != 1) {
            controlsPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, periodPane)
        }

        if (model.nodes.size() == 1) {
            parametersBox.border = BorderFactory.createTitledBorder("Parameter:")

            parametersBox.add(ULCBoxPane.BOX_CENTER_CENTER, new ULCLabel("Kernel bandwidth:"))
            parametersBox.add(ULCBoxPane.BOX_LEFT_CENTER, bandwidthSpinner)
            def lowerRange = new DecimalFormat("0.##").format(model.spinnerModel.minimum)
            def upperRange = new DecimalFormat(",###").format(model.spinnerModel.maximum)
            parametersBox.add(ULCBoxPane.BOX_CENTER_CENTER, new ULCLabel("(Range $lowerRange ... $upperRange)"))

            parametersBox.add(ULCBoxPane.BOX_CENTER_CENTER, applyButton)
            controlsPane.add(ULCBoxPane.BOX_EXPAND_BOTTOM, parametersBox)
        }
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, ulcChart)
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, controlsPane)
    }


    void attachListeners() {
        super.attachListeners()
        bandwidthSpinner.addValueChangedListener([valueChanged: {event -> applyButton.enabled = true }] as IValueChangedListener)
        applyButton.addActionListener([actionPerformed: {event -> model.setBandwith(bandwidthSpinner.getValue()); applyButton.enabled = false}] as IActionListener)
    }
}