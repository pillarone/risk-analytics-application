package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.chart.view.ChartView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*

/**
 * @author fouad jaada
 */

public class ZoomChartViewDialog {

    ULCBoxPane content
    ULCDialog dialog
    ChartView chartView

    ULCTextField xAxisMin
    ULCTextField xAxisMax
    ULCTextField yAxisMin
    ULCTextField yAxisMax

    ULCButton ok
    ULCButton cancel


    public ZoomChartViewDialog(ChartView chartView) {
        this.chartView = chartView

        initComponents()
        attachListeners()

        ULCWindow window = UlcUtilities.getWindowAncestor(chartView.content)
        dialog = new ULCDialog(window, UIUtils.getText(this.class, "title"))
        dialog.setLocationRelativeTo(window)
        dialog.size = new Dimension(400, 210)
        dialog.modal = true
        dialog.contentPane = content
    }

    private void initComponents() {
        content = new ULCBoxPane(3, 0)
        content.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(UIUtils.getText(this.class, 'XAxis') + ":"))
        xAxisMin = new ULCTextField()
        xAxisMax = new ULCTextField()
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, xAxisMin)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, xAxisMax)

        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(UIUtils.getText(this.class, 'YAxis') + ':'))
        yAxisMin = new ULCTextField()
        yAxisMax = new ULCTextField()
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, yAxisMin)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, yAxisMax)

        content.add(new ULCFiller())

        ok = new ULCButton(UIUtils.getText(this.class, 'OK'))
        content.add(ULCBoxPane.BOX_LEFT_CENTER, ok)

        cancel = new ULCButton(UIUtils.getText(this.class, 'Cancel'))
        content.add(ULCBoxPane.BOX_LEFT_CENTER, cancel)
    }


    private void attachListeners() {
        cancel.addActionListener([actionPerformed: { dialog.visible = false; dialog.dispose() }] as IActionListener)
        ok.addActionListener([actionPerformed: {
            chartView.update()
            chartView.zoom(xAxisMin.value, xAxisMax.value, yAxisMin.value, yAxisMax.value)
            dialog.visible = false
            dialog.dispose()
        }] as IActionListener)
    }

}