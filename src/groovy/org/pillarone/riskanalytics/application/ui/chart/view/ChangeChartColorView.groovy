package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.chart.model.ChartViewModel
import org.pillarone.riskanalytics.application.ui.chart.model.DistributionChartsViewModel
import org.pillarone.riskanalytics.application.ui.chart.model.LineChartViewModel
import org.pillarone.riskanalytics.application.ui.chart.model.StackedBarChartViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ChangeChartColorView {

    ULCBoxPane content
    ULCBoxPane seriesPane
    ChartView chartView
    int height = 50


    public ChangeChartColorView(ChartView chartView) {
        this.chartView = chartView;
        initComponents()
    }

    private void initComponents() {
        seriesPane = initColorPane()
        content = new ULCBoxPane(3, 0)
        content.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        ULCScrollPane scrollPane = new ULCScrollPane(seriesPane)
        scrollPane.border = BorderFactory.createEmptyBorder()
        scrollPane.minimumSize = new Dimension(640, getScrollPaneHeight())
        content.add 3, ULCBoxPane.BOX_EXPAND_EXPAND, scrollPane
    }

    protected ULCBoxPane initColorPane() {
        ULCBoxPane seriesPane
        seriesPane = new ULCBoxPane(1, 0)

        chartView?.model?.series?.eachWithIndex {List observations, int keyFigureIndex ->
            observations.eachWithIndex { double[] periods, int periodIndex ->
                ULCBoxPane panel = new ULCBoxPane(3, 0)
                Color ulccolor = UIUtils.toULCColor(chartView.model.seriesColor.getColor(keyFigureIndex, periodIndex))
                GString text = "${chartView?.model?.seriesNames[keyFigureIndex]}: ${chartView.model.getPeriodLabel(periodIndex, true)}"
                ULCLabel label = new ULCLabel(text)
                label.setToolTipText text
                label.setForeground UIUtils.getFontColor(ulccolor)
                panel.setBackground(ulccolor)
                ULCButton button = new ULCButton(UIUtils.getText(ChangeChartColorView.class, "ChangeColor"))
                button.setMaximumSize new Dimension(100, 25)
                button.addActionListener([actionPerformed: {
                    ULCColorChooser colorChooser = new ULCColorChooser(ulccolor);
                    ULCDialog dialog = ULCColorChooser.createDialog(chartView.content, UIUtils.getText(ChangeChartColorView.class, "ColorChooser"), true, colorChooser, new ColorChooserActionListener(colorChooser, chartView.model, panel, label, keyFigureIndex, periodIndex), null);
                    dialog.setVisible(true);
                }] as IActionListener)
                panel.add(ULCBoxPane.BOX_LEFT_TOP, ULCFiller.createHorizontalStrut(2));
                panel.add(ULCBoxPane.BOX_EXPAND_CENTER, label);
                panel.add(ULCBoxPane.BOX_RIGHT_CENTER, button);
                height += 30
                seriesPane.add(ULCBoxPane.BOX_EXPAND_TOP, panel)
            }
            seriesPane.add(2, ULCBoxPane.BOX_EXPAND_TOP, ULCFiller.createVerticalStrut(5))
        }
        seriesPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        return seriesPane
    }

    public void fireModelChanged() {
        chartView.model.fireEvents = true
        chartView.model.fireModelChanged()
    }

    public int getScrollPaneHeight() {
        return Math.max(200, Math.min(height, 800))
    }

}

class ChangePeriodColorView extends ChangeChartColorView {

    public ChangePeriodColorView(ChartView chartView) {
        super(chartView)
    }

    protected ULCBoxPane initColorPane() {
        ULCBoxPane seriesPane
        seriesPane = new ULCBoxPane(1, 0)
        chartView.model.periodCount.times {int periodIndex ->
            ULCBoxPane panel = new ULCBoxPane(3, 0)
            Color ulccolor = UIUtils.toULCColor(chartView.model.seriesColor.getColorByParam(periodIndex))
            GString text = "${chartView.model.getPeriodLabel(periodIndex, true)}"
            ULCLabel label = new ULCLabel(text)
            label.setToolTipText text
            label.setForeground UIUtils.getFontColor(ulccolor)
            panel.setBackground(ulccolor)
            ULCButton button = new ULCButton(UIUtils.getText(ChangeChartColorView.class, "ChangeColor"))
            button.setMaximumSize new Dimension(100, 25)
            button.addActionListener([actionPerformed: {
                ULCColorChooser colorChooser = new ULCColorChooser(ulccolor);
                ULCDialog dialog = ULCColorChooser.createDialog(chartView.content, UIUtils.getText(ChangeChartColorView.class, "ColorChooser"), true, colorChooser, new ColorChooserActionListener(colorChooser, chartView.model, panel, label, -1, periodIndex), null);
                dialog.setVisible(true);
            }] as IActionListener)
            panel.add(ULCBoxPane.BOX_LEFT_TOP, ULCFiller.createHorizontalStrut(2));
            panel.add(ULCBoxPane.BOX_EXPAND_CENTER, label);
            panel.add(ULCBoxPane.BOX_RIGHT_CENTER, button);
            seriesPane.add(ULCBoxPane.BOX_EXPAND_TOP, panel)
            height += 30
        }
        seriesPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        return seriesPane
    }


}

class ChangeSerieColorView extends ChangeChartColorView {

    public ChangeSerieColorView(ChartView chartView) {
        super(chartView)
    }

    protected ULCBoxPane initColorPane() {
        ULCBoxPane seriesPane
        seriesPane = new ULCBoxPane(1, 0)
        chartView.model.means.eachWithIndex {List series, int seriesIndex ->
            ULCBoxPane panel = new ULCBoxPane(3, 0)
            Color ulccolor = UIUtils.toULCColor(chartView.model.seriesColor.getColorByParam(seriesIndex))
            GString text = "${chartView?.model?.seriesNames[seriesIndex]}"
            ULCLabel label = new ULCLabel(text)
            label.setToolTipText text
            label.setForeground UIUtils.getFontColor(ulccolor)
            panel.setBackground(ulccolor)
            ULCButton button = new ULCButton(UIUtils.getText(ChangeChartColorView.class, "ChangeColor"))
            button.setMaximumSize new Dimension(100, 25)
            button.addActionListener([actionPerformed: {
                ULCColorChooser colorChooser = new ULCColorChooser(ulccolor);
                ULCDialog dialog = ULCColorChooser.createDialog(chartView.content, UIUtils.getText(ChangeChartColorView.class, "ColorChooser"), true, colorChooser, new ColorChooserActionListener(colorChooser, chartView.model, panel, label, seriesIndex, -1), null);
                dialog.setVisible(true);
            }] as IActionListener)
            panel.add(ULCBoxPane.BOX_LEFT_TOP, ULCFiller.createHorizontalStrut(2));
            panel.add(ULCBoxPane.BOX_EXPAND_CENTER, label);
            panel.add(ULCBoxPane.BOX_RIGHT_CENTER, button);
            seriesPane.add(ULCBoxPane.BOX_EXPAND_TOP, panel)
            height += 30
        }
        seriesPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        return seriesPane
    }


}


class ColorChooserActionListener implements IActionListener {

    ULCColorChooser colorChooser
    ChartViewModel model
    ULCBoxPane panel
    ULCLabel label
    int keyFigureIndex
    int periodIndex

    public ColorChooserActionListener(ULCColorChooser colorChooser, ChartViewModel model, ULCBoxPane panel, ULCLabel label, int keyFigureIndex, int periodIndex) {
        this.colorChooser = colorChooser;
        this.model = model
        this.panel = panel
        this.label = label
        this.keyFigureIndex = keyFigureIndex
        this.periodIndex = periodIndex
    }

    public void actionPerformed(ActionEvent event) {
        com.ulcjava.base.application.util.Color newColor = colorChooser.getColor();
        panel.setBackground newColor
        label.setForeground UIUtils.getFontColor(newColor)
        changeColor model, newColor
    }

    private void changeColor(DistributionChartsViewModel model, com.ulcjava.base.application.util.Color newColor) {
        model.strategyModel.seriesColor.changeColor keyFigureIndex, periodIndex, UIUtils.toAwtColor(newColor)
    }

    private void changeColor(StackedBarChartViewModel model, com.ulcjava.base.application.util.Color newColor) {
        model.seriesColor.changeColor keyFigureIndex, UIUtils.toAwtColor(newColor)
    }

    private void changeColor(LineChartViewModel model, com.ulcjava.base.application.util.Color newColor) {
        model.seriesColor.changeColor keyFigureIndex, UIUtils.toAwtColor(newColor)
    }

    private void changeColor(def model, com.ulcjava.base.application.util.Color newColor) {
        model.seriesColor.changeColor keyFigureIndex, periodIndex, UIUtils.toAwtColor(newColor)
    }
}
