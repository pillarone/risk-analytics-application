package org.pillarone.riskanalytics.application.ui.chart.model

import java.awt.BasicStroke
import java.awt.Color
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.annotations.XYLineAnnotation
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.title.LegendTitle
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.ui.HorizontalAlignment
import org.jfree.ui.RectangleEdge
import org.pillarone.riskanalytics.application.dataaccess.function.Percentile
import org.pillarone.riskanalytics.application.dataaccess.function.ResultFunction
import org.pillarone.riskanalytics.application.reports.bean.ReportChartDataBean
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.ChartInsetWriter
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun

/**
 * Created by IntelliJ IDEA.
 * User: martin.melchior
 * Date: 20.11.2008
 * Time: 10:44:28
 * To change this template use File | Settings | File Templates.
 */
abstract class KernelEstimatorChartViewModel extends ChartViewModel {

    protected List<Double> observations
    protected int rawNodeIndex = 0
    protected int rawPeriodIndex = 0

    protected List<Double> mins
    protected List<Double> maxs
    protected List<Double> means
    protected List<Double> stdDevs
    protected List<Double> IQRs

    protected double min
    protected double max
    protected double mean
    protected double stdDev
    protected double IQR

    protected double maxY

    Map seriesCache = [:]

    public KernelEstimatorChartViewModel() {}

    public KernelEstimatorChartViewModel(String title, SimulationRun simulationRun, List<SimpleTableTreeNode> nodes, double insetHeight) {
        super(title, simulationRun, nodes, insetHeight)
        chartProperties = new ChartProperties(title: title, xAxisTitle: 'values', yAxisTitle: getYAxisLabel(), showLegend: true)
    }

    abstract String getYAxisLabel()

    protected void storeInCache(int keyFigureIndex, int periodIndex, def series) {
        seriesCache[keyFigureIndex, periodIndex] = series
    }

    protected def getFromCache(int keyFigureIndex, int periodIndex) {
        return seriesCache[keyFigureIndex, periodIndex]
    }

    public JFreeChart getChart() {
        if (!onlyStochasticSeries) {
            return chartInsetWriter.createErrorMessageChart("at least one series is constant")
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart(chartProperties.title, chartProperties.xAxisTitle, chartProperties.yAxisTitle, dataset, PlotOrientation.VERTICAL, chartProperties.showLegend, true, false);

        double upperBound = (-1) * Double.MAX_VALUE
        double lowerBound = Double.MAX_VALUE
        int seriesIndex = 0

        series.eachWithIndex {List observations, int keyFigureIndex ->
            observations.eachWithIndex {List<Double> periods, int periodIndex ->
                if (showLine[keyFigureIndex, periodIndex] && notStochasticSeries[seriesNames[keyFigureIndex], periodIndex] == null) {
                    XYSeries currentSeries = null

                    this.observations = periods
                    rawNodeIndex = keyFigureIndex
                    rawPeriodIndex = periodIndex
                    min = mins[keyFigureIndex][periodIndex]
                    max = maxs[keyFigureIndex][periodIndex]
                    mean = means[keyFigureIndex][periodIndex]
                    stdDev = stdDevs[keyFigureIndex][periodIndex]
                    IQR = IQRs[keyFigureIndex][periodIndex]

                    if (getFromCache(keyFigureIndex, periodIndex)) {
                        currentSeries = getFromCache(keyFigureIndex, periodIndex)
                    } else {
                        currentSeries = createSeries("${seriesNames[keyFigureIndex]} for ${getPeriodLabel(periodIndex)}")
                        storeInCache(keyFigureIndex, periodIndex, currentSeries)
                    }

                    upperBound = Math.max(upperBound, (Double) currentSeries.getX(currentSeries.getItemCount() - 1))
                    lowerBound = Math.min(lowerBound, (Double) currentSeries.getX(0))
                    dataset.addSeries currentSeries

                    BasicStroke thickLine = new BasicStroke(chartLineThickness)
                    chart.getPlot().getRenderer(0).setSeriesStroke(seriesIndex, thickLine)
                    seriesIndex++
                }
            }
        }

        chart.getXYPlot().setDataset 0, dataset

        if (seriesIndex == 1) {
            addHistogram(chart, "", lowerBound, upperBound)
            chartInsetWriter.addInset(chart, {ChartInsetWriter w -> writeInsetContent w})
        }

        seriesIndex = 0
        maxY = chart.getXYPlot().getRangeAxis().getRange().upperBound
        series.eachWithIndex {List observations, int keyFigureIndex ->
            observations.eachWithIndex {List<Double> periods, int periodIndex ->
                if (showLine[keyFigureIndex, periodIndex]) {
                    mean = means[keyFigureIndex][periodIndex]
                    stdDev = stdDevs[keyFigureIndex][periodIndex]
                    chart.getPlot().getRenderer(0).setSeriesPaint seriesIndex, seriesColor.getColor(keyFigureIndex, periodIndex)
                    addStatisticalValues(chart, seriesColor.getColor(keyFigureIndex, periodIndex), seriesIndex)
                    seriesIndex++
                }
            }
        }

        //set legend position
        if (chartProperties.showLegend) {
            LegendTitle legend = chart.getLegend();
            legend.setPosition(RectangleEdge.BOTTOM);
            legend.setHorizontalAlignment(HorizontalAlignment.LEFT);
            legend.setMargin 5, 50, 5, 5
        }
        else {
            chart.removeLegend()
        }

        return chart
    }

    Map getDataTable() {
        dataExportMode = true
        Map columns = [:]

        XYSeries xSeries = createSeries(seriesNames[0])
        List xValues = []


        (xSeries.itemCount).times {
            xValues << xSeries.getX(it)
        }
        columns["x"] = xValues

        series.eachWithIndex {List observations, int keyFigureIndex ->
            observations.eachWithIndex {List<Double> periods, int periodIndex ->
                if (showLine[keyFigureIndex, periodIndex]) {
                    List rowValues = []
                    String seriesName = seriesNames[keyFigureIndex]
                    this.observations = periods
                    XYSeries rowSeries = createSeries(seriesName)
                    rowSeries.itemCount.times {
                        rowValues << rowSeries.getY(it)
                    }
                    columns[seriesName + " for " + getPeriodLabel(periodIndex)] = rowValues
                }
            }
        }
        dataExportMode = false
        return columns
    }

    public List<ReportChartDataBean> getReportData(int currentPeriod) {
        dataExportMode = true
        List<ReportChartDataBean> list = []

        series.eachWithIndex {List values, int SeriesIndex ->
            values[currentPeriod].eachWithIndex {double y, int x ->
                list << new ReportChartDataBean(x: x, y: y, period: currentPeriod, line: seriesNames[SeriesIndex])
            }
        }

        dataExportMode = false
        return list
    }

    protected void loadData() {
        mins = []
        maxs = []
        means = []
        stdDevs = []
        IQRs = []

        nodes.each {def node ->
            List periods = []
            List minsP = []
            List maxsP = []
            List meansP = []
            List stdDevsP = []
            List IQRsP = []
            periodCount.times {int periodIndex ->
                onlyStochasticSeries = onlyStochasticSeries && ResultAccessor.hasDifferentValues(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)
                Percentile percentile = new Percentile(percentile: 75)
                Double per75 = percentile.evaluate(simulationRun, periodIndex, node)
                percentile = new Percentile(percentile: 25)
                Double per25 = percentile.evaluate(simulationRun, periodIndex, node)
                if (onlyStochasticSeries) {
                    periods << ResultAccessor.getValues(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)
                    minsP << ResultAccessor.getMin(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)
                    maxsP << ResultAccessor.getMax(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)
                    meansP << ResultAccessor.getMean(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)
                    stdDevsP << ResultAccessor.getStdDev(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)

                    if (per75 != null && per25 != null) {
                        IQRsP << per75 - per25
                    }
                } else {
                    notStochasticSeries[node.getShortDisplayPath(nodes), periodIndex] = true
                    periods << []
                    minsP << 0
                    maxsP << 0
                    meansP << 0
                    stdDevsP << 0
                    if (per75 != null && per25 != null) {
                        IQRsP << 0
                    }
                }
                onlyStochasticSeries = true
            }
            series << periods
            mins << minsP
            maxs << maxsP
            means << meansP
            stdDevs << stdDevsP
            IQRs << IQRsP
            seriesNames << node.getShortDisplayPath(nodes)
        }
    }

    protected void addStatisticalValues(JFreeChart chart, Color c, double i) {
        Color meanColor = new Color((int) (c.getRed() * 0.7),
                (int) (c.getGreen() * 0.7),
                (int) (c.getBlue() * 0.7))

        Color stdDevColor = new Color((int) (c.getRed() * 0.7),
                (int) (c.getGreen() * 0.7),
                (int) (c.getBlue() * 0.7))

        final double HEIGHT = maxY * 0.1

        XYLineAnnotation meanLine = new XYLineAnnotation(mean, 0, mean, (maxY + HEIGHT * i) / 20, new BasicStroke(1.0f), meanColor)
        chart.getXYPlot().addAnnotation(meanLine)

        XYLineAnnotation stdDevLine = new XYLineAnnotation(mean - stdDev, (maxY + HEIGHT * i) / 40, mean + stdDev, (maxY + HEIGHT * i) / 40, new BasicStroke(1.0f), stdDevColor)
        chart.getXYPlot().addAnnotation(stdDevLine)

        XYLineAnnotation stdDevLowerLineEnd = new XYLineAnnotation(mean - stdDev, (maxY * 3 + HEIGHT * i) / 80, mean - stdDev, (maxY + HEIGHT * i) / 80, new BasicStroke(1.0f), stdDevColor)
        chart.getXYPlot().addAnnotation(stdDevLowerLineEnd)

        XYLineAnnotation stdDevUpperLineEnd = new XYLineAnnotation(mean + stdDev, (maxY * 3 + HEIGHT * i) / 80, mean + stdDev, (maxY + HEIGHT * i) / 80, new BasicStroke(1.0f), stdDevColor)
        chart.getXYPlot().addAnnotation(stdDevUpperLineEnd)
    }

    abstract protected void addHistogram(JFreeChart chart, String legendTitle, double lowerBound, double upperBound)

    abstract protected XYSeries createSeries(String legendTitle)

    abstract protected void addToSeries(XYSeries series)
}