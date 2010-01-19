package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.ui.chart.model.ChartProperties
import org.pillarone.riskanalytics.application.ui.chart.view.ChartView
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.util.LocaleResources

class ChartPropertiesDialog {

    ULCBoxPane content
    ULCDialog dialog
    ChartView chartView

    ULCTextField chartTitle
    ULCTextField xAxisTitle
    ULCTextField yAxisTitle
    ULCCheckBox showLegend

    ULCButton ok
    ULCButton cancel

    List enabledFields

    public ChartPropertiesDialog(ChartView chartView, String title, List enabledFields = [true, true, true, true]) {
        this.chartView = chartView
        this.enabledFields = enabledFields

        initComponents()
        attachListeners()

        ULCWindow window = UlcUtilities.getWindowAncestor(chartView.content)
        dialog = new ULCDialog(window, getText("title"))
        dialog.setLocationRelativeTo(window)
        dialog.size = new Dimension(400, 210)
        dialog.modal = true
        dialog.contentPane = content
    }

    private void initComponents() {
        content = new ULCBoxPane(2, 0)
        content.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        ULCLabel title = new ULCLabel(getText("chartProperties"))
        title.font = title.font.deriveFont(Font.BOLD, 16f)
        content.add(2, title)

        content.add(2, new ULCFiller(1, 10))

        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("chartTitle") + ":"))

        chartTitle = new ULCTextField(chartView.model.chartProperties.title)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, chartTitle)

        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText('XAxisTitle') + ":"))
        xAxisTitle = new ULCTextField(chartView.model.chartProperties.xAxisTitle)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, xAxisTitle)

        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText('YAxisTitle') + ':'))
        yAxisTitle = new ULCTextField(chartView.model.chartProperties.yAxisTitle)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, yAxisTitle)

        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText('ShowLegend') + ':'))
        showLegend = new ULCCheckBox('', chartView.model.chartProperties.showLegend)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, showLegend)

        content.add(2, new ULCFiller(1, 10))

        ok = new ULCButton(getText('OK'))
        content.add(ULCBoxPane.BOX_LEFT_CENTER, ok)

        cancel = new ULCButton(getText('cancel'))
        content.add(ULCBoxPane.BOX_LEFT_CENTER, cancel)

        enableFields()
    }

    private void enableFields() {
        chartTitle.enabled = enabledFields[0]
        xAxisTitle.enabled = enabledFields[1]
        yAxisTitle.enabled = enabledFields[2]
        showLegend.enabled = enabledFields[3]
    }

    private void attachListeners() {
        cancel.addActionListener([actionPerformed: { dialog.visible = false; dialog.dispose() }] as IActionListener)
        ok.addActionListener([actionPerformed: {
            ChartProperties chartProperties = new ChartProperties()
            chartProperties.title = chartTitle.text
            chartProperties.xAxisTitle = xAxisTitle.text
            chartProperties.yAxisTitle = yAxisTitle.text
            chartProperties.showLegend = showLegend.selected
            chartView.model.chartProperties = chartProperties
            chartView.model.fireModelChanged()

            dialog.visible = false
            dialog.dispose()
        }] as IActionListener)
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ChartPropertiesDialog." + key);
    }
}