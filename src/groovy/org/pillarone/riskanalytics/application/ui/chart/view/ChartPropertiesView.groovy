package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.ui.chart.model.ChartProperties
import org.pillarone.riskanalytics.application.ui.chart.model.LineChartViewModel
import org.pillarone.riskanalytics.application.ui.chart.model.ScatterChartViewModel
import org.pillarone.riskanalytics.application.ui.chart.model.StackedBarChartViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.base.application.*

class ChartPropertiesView {

    ULCBoxPane content
    ChartView chartView

    ULCTextField chartTitle
    ULCTextField xAxisTitle
    ULCTextField yAxisTitle
    ULCCheckBox showLegend


    List enabledFields
    boolean changed = false

    public ChartPropertiesView(ChartView chartView, String title, List enabledFields = [true, true, true, true]) {
        this.chartView = chartView
        this.enabledFields = enabledFields

        initComponents()
        attachListeners()
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

        content.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())


        enableFields()
    }

    private void enableFields() {
        chartTitle.enabled = enabledFields[0]
        xAxisTitle.enabled = enabledFields[1]
        yAxisTitle.enabled = enabledFields[2]
        showLegend.enabled = enabledFields[3]
    }

    private void attachListeners() {
        chartTitle.addValueChangedListener([valueChanged: {ValueChangedEvent event -> changed = true}] as IValueChangedListener)
        xAxisTitle.addValueChangedListener([valueChanged: {ValueChangedEvent event -> changed = true}] as IValueChangedListener)
        yAxisTitle.addValueChangedListener([valueChanged: {ValueChangedEvent event -> changed = true}] as IValueChangedListener)
        showLegend.addValueChangedListener([valueChanged: {ValueChangedEvent event -> changed = true}] as IValueChangedListener)
    }

    public void fireModelChanged() {
        if (changed) {
            ChartProperties chartProperties = new ChartProperties()
            chartProperties.title = chartTitle.text
            chartProperties.xAxisTitle = xAxisTitle.text
            chartProperties.yAxisTitle = yAxisTitle.text
            chartProperties.showLegend = showLegend.selected
            chartView.model.chartProperties = chartProperties
            chartView.model.fireModelChanged()
        }

    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ChartPropertiesView." + key);
    }
}

class PropertiesDialog {
    ULCBoxPane content
    ULCTabbedPane tabbedPane
    ChartPropertiesView chartPropertiesDialog
    ChangeChartColorView changeChartColorDialog
    ULCDialog dialog
    ULCButton cancel
    ULCButton apply


    public PropertiesDialog(ChartView chartView, String title, List enabledFields = [true, true, true, true]) {
        chartPropertiesDialog = new ChartPropertiesView(chartView, title, enabledFields)
        if (chartView.model.isChangeColorEnabled())
            changeChartColorDialog = ChangeChartColorFactory.getInstance(chartView)

        initComponents()
        attachListeners()

        ULCWindow window = UlcUtilities.getWindowAncestor(chartView.content)
        dialog = new ULCDialog(window, UIUtils.getText(PropertiesDialog.class, "title"))
        dialog.setLocationRelativeTo(window)
        dialog.size = new Dimension(650, changeChartColorDialog ? changeChartColorDialog.getScrollPaneHeight() + 150 : 350)
        dialog.modal = true
        dialog.contentPane = content

    }

    private void initComponents() {
        content = new ULCBoxPane(3, 0)
        content.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        tabbedPane = new ULCTabbedPane(ULCTabbedPane.TOP);
        tabbedPane.addTab(UIUtils.getText(PropertiesDialog.class, "labels"), chartPropertiesDialog.content)
        if (changeChartColorDialog)
            tabbedPane.addTab(UIUtils.getText(PropertiesDialog.class, "Colours"), changeChartColorDialog.content)


        cancel = new ULCButton(UIUtils.getText(ChangeChartColorView.class, "cancel"))
        apply = new ULCButton(UIUtils.getText(ChangeChartColorView.class, "apply"))

        content.add 3, ULCBoxPane.BOX_EXPAND_EXPAND, tabbedPane
        content.add ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller()
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, apply)
        content.add(ULCBoxPane.BOX_LEFT_BOTTOM, cancel)

    }

    private void attachListeners() {

        apply.addActionListener([actionPerformed: {
            chartPropertiesDialog.fireModelChanged()
            if (changeChartColorDialog)
                changeChartColorDialog.fireModelChanged()
            dialog.visible = false;
            dialog.dispose()
        }] as IActionListener)
        cancel.addActionListener([actionPerformed: { dialog.visible = false; dialog.dispose() }] as IActionListener)
    }
}

class ChangeChartColorFactory {

    static ChangeChartColorView getInstance(ChartView chartView) {
        return createContent(chartView, chartView.model)
    }

    private static ChangeChartColorView createContent(ChartView chartView, def model) {
        new ChangeChartColorView(chartView)
    }

    private static ChangeChartColorView createContent(ChartView chartView, ScatterChartViewModel model) {
        new ChangePeriodColorView(chartView)
    }

    private static ChangeChartColorView createContent(ChartView chartView, StackedBarChartViewModel model) {
        new ChangeSerieColorView(chartView)
    }

    private static ChangeChartColorView createContent(ChartView chartView, LineChartViewModel model) {
        new ChangeSerieColorView(chartView)
    }


}
