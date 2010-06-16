package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.serializable.IActionListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.chart.model.ChartViewModel
import org.pillarone.riskanalytics.application.ui.chart.model.DistributionChartsViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ChangeChartColorDialog {

    ULCBoxPane content
    ULCBoxPane seriesPane
    ULCDialog dialog
    ChartView chartView
    ULCButton cancel
    ULCButton apply


    public ChangeChartColorDialog(ChartView chartView) {
        this.chartView = chartView;

        initComponents()
        attachListeners()

        ULCWindow window = UlcUtilities.getWindowAncestor(chartView.content)
        dialog = new ULCDialog(window, "Title: Change Color")
        dialog.setLocationRelativeTo(window)
        dialog.size = new Dimension(600, 500)
        dialog.modal = true
        dialog.contentPane = content
    }

    private void initComponents() {
        seriesPane = new ULCBoxPane(1, 0)

        chartView?.model?.series?.eachWithIndex {List observations, int keyFigureIndex ->
            observations.eachWithIndex {List<Double> periods, int periodIndex ->
                ULCBoxPane panel = new ULCBoxPane(2, 0)
                com.ulcjava.base.application.util.Color ulccolor = UIUtils.toULCColor(chartView.model.seriesColor.getColor(keyFigureIndex, periodIndex))
                GString text = "${chartView?.model?.seriesNames[keyFigureIndex]} for ${chartView.model.getPeriodLabel(periodIndex)}"
                ULCLabel label = new ULCLabel(text)
                label.setToolTipText text
                panel.setBackground(ulccolor)
                ULCButton button = new ULCButton("Change Color")
                button.addActionListener([actionPerformed: {
                    ULCColorChooser colorChooser = new ULCColorChooser(ulccolor);
                    ULCDialog dialog = ULCColorChooser.createDialog(chartView.content, "My Color Chooser", true, colorChooser, new ColorChooserActionListener(colorChooser, chartView.model, panel, keyFigureIndex, periodIndex), null);
                    dialog.setVisible(true);
                }] as IActionListener)
                panel.add(ULCBoxPane.BOX_EXPAND_TOP, label);
                panel.add(ULCBoxPane.BOX_RIGHT_TOP, button);
                seriesPane.add(ULCBoxPane.BOX_EXPAND_TOP, panel)
            }
        }

        cancel = new ULCButton(UIUtils.getText(this.class, "cancel"))
        apply = new ULCButton(UIUtils.getText(this.class, "apply"))
        content = new ULCBoxPane(3, 0)
        content.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        ULCScrollPane scrollPane = new ULCScrollPane(seriesPane)
        scrollPane.minimumSize = new Dimension(560, 400)
        content.add 3, ULCBoxPane.BOX_EXPAND_EXPAND, scrollPane
        content.add ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller()
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, apply)
        content.add(ULCBoxPane.BOX_LEFT_BOTTOM, cancel)
    }

    private void attachListeners() {
        apply.addActionListener([actionPerformed: {
            chartView.model.fireEvents = true
            chartView.model.fireModelChanged()
            dialog.visible = false;
            dialog.dispose()
        }] as IActionListener)
        cancel.addActionListener([actionPerformed: { dialog.visible = false; dialog.dispose() }] as IActionListener)
    }
}

class ColorChooserActionListener implements IActionListener {

    ULCColorChooser colorChooser
    ChartViewModel model
    ULCBoxPane panel
    int keyFigureIndex
    int periodIndex

    public ColorChooserActionListener(ULCColorChooser colorChooser, ChartViewModel model, ULCBoxPane panel, int keyFigureIndex, int periodIndex) {
        this.colorChooser = colorChooser;
        this.model = model
        this.panel = panel
        this.keyFigureIndex = keyFigureIndex
        this.periodIndex = periodIndex
    }

    public void actionPerformed(ActionEvent event) {
        com.ulcjava.base.application.util.Color newColor = colorChooser.getColor();
        panel.setBackground newColor
        if (model instanceof DistributionChartsViewModel) {
            model.strategyModel.seriesColor.changeColor keyFigureIndex, periodIndex, UIUtils.toAwtColor(newColor)
        } else {
            model.seriesColor.changeColor keyFigureIndex, periodIndex, UIUtils.toAwtColor(newColor)
        }
    }
}
