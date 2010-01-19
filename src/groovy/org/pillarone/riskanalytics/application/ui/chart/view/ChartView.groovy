package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import com.ulcjava.base.application.util.Dimension
import java.awt.geom.Rectangle2D
import java.awt.geom.Rectangle2D.Double
import org.jfree.chart.ChartRenderingInfo
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.chart.action.ChartDataExportAction
import org.pillarone.riskanalytics.application.ui.chart.action.ChartPictureExportAction
import org.pillarone.riskanalytics.application.ui.chart.model.ChartViewModel
import org.pillarone.riskanalytics.application.ui.chart.view.ChartPropertiesDialog
import org.pillarone.riskanalytics.application.ui.chart.view.ZoomChartViewDialog
import com.canoo.ulc.community.jfreechart.server.*
import com.ulcjava.base.application.*

class ChartView implements IModelChangedListener {
    int width = 1, height = 1

    ULCBoxPane content
    ChartViewModel model
    ULCJFreeChart ulcChart
    ULCBoxPane controlsPane
    List periodToggleButtons = []
    List periodCheckBoxes = []
    List keyFigureToggleButtons = []
    ULCToggleButton allPeriods

    public ChartView() {}

    public ChartView(ChartViewModel model) {
        this.@model = model
        model.addListener this
        content = new ULCBoxPane(1, 0)
        content.name = "ChartBox"
        initComponents()
        layoutComponents()
        attachListeners()
        update()

        if (!ulcChart.chart) {
            content = null
        }
    }

    protected void initComponents() {
        ulcChart = new ULCJFreeChart()
        ULCPopupMenu resultNodePopup = new ULCPopupMenu()
        resultNodePopup.add(new ULCMenuItem(new ChartPictureExportAction(this)))
        resultNodePopup.add(new ULCMenuItem(new ChartDataExportAction(this)))
        resultNodePopup.addSeparator()
        resultNodePopup.add(new ULCMenuItem(new ZoomAction(this)))
        resultNodePopup.addSeparator()
        resultNodePopup.add(new ULCMenuItem(new ResetZoomAction(this)))
        resultNodePopup.addSeparator()
        resultNodePopup.add(new ULCMenuItem(new OpenChartPropertiesAction(this)))
        ulcChart.setComponentPopupMenu(resultNodePopup)
        controlsPane = new ULCBoxPane(1, 0)
    }

    void layoutComponents() {
        if (model.periodCount != 1 || model.seriesNames.size() != 1) {
            if (model.periodCount <= 5) {
                controlsPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, periodPane)
            } else {
                controlsPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, simplePeriodPane)
            }
        } else {
            model.setSeriesVisibility(0, 0, true)
        }
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, ulcChart)
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, controlsPane)
    }

    protected ULCBoxPane getSimplePeriodPane() {
        ULCBoxPane periodBox = new ULCBoxPane(2, model.seriesNames.size() + 1)
        allPeriods = new ULCToggleButton(getText("all"))
        allPeriods.addActionListener([actionPerformed: {ActionEvent event ->
            if (allPeriods.selected) {
                model.selectAll(true)
            } else {
                model.selectAll(false)
            }
        }] as IActionListener)
        periodBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, allPeriods)
        periodBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller(500, 20))

        model.seriesNames.eachWithIndex {String seriesName, int seriesIndex ->
            ULCToggleButton button = new ULCToggleButton(seriesName)
            button.addActionListener([actionPerformed: {ActionEvent event ->
                if (button.selected) {
                    model.selectAllFromKeyFigure(seriesIndex)
                } else {
                    model.deselectAllFromKeyFigure(seriesIndex)
                }
            }] as IActionListener)
            button.selected = model.allFromOneKeyFigureSelected(seriesIndex)
            periodBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, button)
            periodBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
            keyFigureToggleButtons << button
        }

        periodBox.border = BorderFactory.createTitledBorder("")
        return periodBox
    }

    protected ULCBoxPane getPeriodPane() {
        ULCBoxPane periodBox = new ULCBoxPane(model.periodCount + 1, model.seriesNames.size() + 1)
        allPeriods = new ULCToggleButton(getText("all"))
        allPeriods.addActionListener([actionPerformed: {ActionEvent event ->
            if (allPeriods.selected) {
                model.selectAll(true)
            } else {
                model.selectAll(false)
            }
        }] as IActionListener)
        periodBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, allPeriods)

        model.periodCount.times {int index ->
            String periodLabel = model.getPeriodLabel(index)
            ULCToggleButton button = new ULCToggleButton(periodLabel)
            button.addActionListener([actionPerformed: {ActionEvent event ->
                if (button.selected) {
                    model.selectAllFromPeriod(index)
                } else {
                    model.deselectAllFromPeriod(index)
                }
            }] as IActionListener)
            button.selected = model.allFromOnePeriodSelected(index)
            periodBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, button)
            periodToggleButtons << button
        }

        model.seriesNames.eachWithIndex {String seriesName, int seriesIndex ->
            ULCToggleButton button = new ULCToggleButton(seriesName)
            button.addActionListener([actionPerformed: {ActionEvent event ->
                if (button.selected) {
                    model.selectAllFromKeyFigure(seriesIndex)
                } else {
                    model.deselectAllFromKeyFigure(seriesIndex)
                }
            }] as IActionListener)
            button.selected = model.allFromOneKeyFigureSelected(seriesIndex)
            periodBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, button)
            keyFigureToggleButtons << button
            model.periodCount.times {int periodIndex ->
                ULCCheckBox checkBox = new ULCCheckBox()
                checkBox.addValueChangedListener([valueChanged: {ValueChangedEvent event -> model.setSeriesVisibility(seriesIndex, periodIndex, event.source.isSelected())}] as IValueChangedListener)
                checkBox.name = "Series${seriesName}Period${periodIndex}CheckBox"
                if (periodIndex == 0) { checkBox.selected = true }
                periodBox.add(ULCBoxPane.BOX_CENTER_EXPAND, checkBox)
                periodCheckBoxes << [seriesIndex, periodIndex, checkBox]
            }
        }

        periodBox.border = BorderFactory.createTitledBorder("")
        return periodBox
    }


    void attachListeners() {
        ulcChart.addChartComponentListener([chartResized: {UlcChartComponentEvent event ->
            Dimension size = event.getChartComponentSize()
            height = size.getHeight()
            width = size.getWidth()
            if (width < 600) {
                model.drawLegend = false
            } else {
                model.drawLegend = true
            }
            update()
        }] as IChartComponentListener)

        ulcChart.showHorizontalTraceLine = true
        ulcChart.showVerticalTraceLine = true
        ulcChart.addChartEventListener([chartClicked: {def d ->}, chartEntityClicked: {def d ->},
                chartZoomed: {UlcChartEvent event ->
                    if (event.getWidth() < 0) {
                        update()
                    }
                }] as IChartEventListener)
    }

    public void update() {
        model.fireEvents = false
        periodToggleButtons.eachWithIndex {ULCToggleButton button, int index ->
            button.selected = model.allFromOnePeriodSelected(index)
        }
        keyFigureToggleButtons.eachWithIndex {ULCToggleButton button, int index ->
            button.selected = model.allFromOneKeyFigureSelected(index)
        }
        periodCheckBoxes.each {List l ->
            l[2].selected = model.getSeriesVisibility(l[0], l[1])
        }
        allPeriods?.selected = model.allSelected()
        JFreeChart chart = model.getChart()
        if (chart) {
            ulcChart.setChart(chart, new Dimension(width, height))
        }
        ulcChart.repaint()
        model.fireEvents = true
    }

    public void modelChanged() {
        update()
    }

    public void writeChartInStream(OutputStream stream) {
        try {
            ChartRenderingInfo chartRenderingInfo = new ChartRenderingInfo();
            JFreeChart zoomedChart = ulcChart.getChart()
            ChartUtilities.writeChartAsPNG(stream, zoomedChart, width,
                    height, chartRenderingInfo);
        } catch (IOException e) {}
    }

    public void zoom(String xMin, String xMax, String yMin, String yMax) {
        double x = valueT2Java2D(xMin, true);
        double y = valueT2Java2D(yMin, false)
        double w = getDistance(xMin, xMax, true)
        double h = getDistance(yMin, yMax, false)
        Rectangle2D rectangle = new Rectangle2D.Double(x, y, w, h);
        ulcChart.zoom(rectangle)
        ulcChart.repaint()
        model.fireEvents = true
    }

    private double valueT2Java2D(String value, boolean isDomainAxis) {
        try {
            Rectangle2D plotArea = ulcChart.getChartRenderingInfo().getPlotInfo().getDataArea()
            XYPlot pl = (XYPlot) model.getChart().getPlot(); // your plot
            return isDomainAxis ? pl.getDomainAxis().valueToJava2D(java.lang.Double.valueOf(value), plotArea, pl.getDomainAxisEdge()) : pl.getRangeAxis().valueToJava2D(java.lang.Double.valueOf(value), plotArea, pl.getRangeAxisEdge());
        } catch (Exception ex) {
        }
        return isDomainAxis ? ulcChart.getChartRenderingInfo().getPlotInfo().getDataArea().getMinX() : ulcChart.getChartRenderingInfo().getPlotInfo().getDataArea().getMinY();
    }

    private double getDistance(String dMin, String dMax, boolean isDomainAxis) {
        double d = 0.0;
        try {
            d = valueT2Java2D(dMax, isDomainAxis) - valueT2Java2D(dMin, isDomainAxis)

        } catch (Exception ex) { }
        if (java.lang.Double.compare(0.0, d) == 0) {
            if (isDomainAxis) {
                d = ulcChart.getChartRenderingInfo().getPlotInfo().getDataArea().getMaxX() - ulcChart.getChartRenderingInfo().getPlotInfo().getDataArea().getMinX();
            } else {
                d = ulcChart.getChartRenderingInfo().getPlotInfo().getDataArea().getMaxY() - ulcChart.getChartRenderingInfo().getPlotInfo().getDataArea().getMinY();
            }
        }
        return Math.abs(d)
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ChartView." + key);
    }

}



class ResetZoomAction extends ResourceBasedAction {
    ChartView chartView

    public ResetZoomAction(ChartView chartView) {
        super("ResetZoom")
        this.@chartView = chartView
    }

    public void doActionPerformed(ActionEvent event) {
        chartView.update()
    }
}

class ZoomAction extends ResourceBasedAction {
    ChartView chartView

    public ZoomAction(ChartView chartView) {
        super("Zoom")
        this.@chartView = chartView
    }

    public void doActionPerformed(ActionEvent event) {
        //chartView.update()
        new ZoomChartViewDialog(chartView).dialog.visible = true
    }
}


class OpenChartPropertiesAction extends ResourceBasedAction {

    ChartView chartView
    List enabledFields

    public OpenChartPropertiesAction(ChartView chartView, List enabledFields = [true, true, true, true]) {
        super("ChartProperties")
        this.@chartView = chartView
        this.@enabledFields = enabledFields
    }

    public void doActionPerformed(ActionEvent event) {
        new ChartPropertiesDialog(chartView, '', enabledFields).dialog.visible = true
    }

}