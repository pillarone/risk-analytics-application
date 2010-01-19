package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.event.IActionListener
import java.text.DecimalFormat
import org.pillarone.riskanalytics.application.ui.chart.model.PDFChartViewModel
import com.ulcjava.base.application.*

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class PDFPropertiesViewDialog {
    ULCBoxPane content
    ULCSpinner bandwidthSpinner
    ULCButton applyButton
    ULCBoxPane parametersBox
    PDFChartViewModel model
    ULCRootPane rootPane
    private ULCDialog dialog


    public PDFPropertiesViewDialog(PDFChartViewModel model, ULCRootPane rootPane) {
        this.@rootPane = rootPane
        this.@model = model
        initComponents()
        layoutComponents()
        atachListeners()
        dialog.visible = true
    }

    protected initComponents() {
        content = new ULCBoxPane(1, 0)
        parametersBox = new ULCBoxPane(4, 0)
        bandwidthSpinner = new ULCSpinner(model.getSpinnerModel())
        applyButton = new ULCButton("ok")

        dialog = new ULCDialog(rootPane, "Properties")
        dialog.setLocationRelativeTo(rootPane)
    }

    protected layoutComponents() {
        parametersBox.border = BorderFactory.createTitledBorder("Parameter:")

        parametersBox.add(ULCBoxPane.BOX_CENTER_CENTER, new ULCLabel("Kernel bandwidth:"))
        parametersBox.add(ULCBoxPane.BOX_LEFT_CENTER, bandwidthSpinner)
        def lowerRange = new DecimalFormat("0.##").format(model.spinnerModel.minimum)
        def upperRange = new DecimalFormat(",###").format(model.spinnerModel.maximum)
        parametersBox.add(ULCBoxPane.BOX_CENTER_CENTER, new ULCLabel("(Range $lowerRange ... $upperRange)"))

        parametersBox.add(ULCBoxPane.BOX_CENTER_CENTER, applyButton)
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, parametersBox)

        dialog.add(content)
    }

    protected atachListeners() {
        applyButton.addActionListener([actionPerformed: {event -> model.setBandwith((double) bandwidthSpinner.getValue()); dialog.dispose() }] as IActionListener)
    }

}

