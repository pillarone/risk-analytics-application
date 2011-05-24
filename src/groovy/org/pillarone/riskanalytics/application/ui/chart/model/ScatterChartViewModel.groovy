package org.pillarone.riskanalytics.application.ui.chart.model

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Rectangle
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.annotations.XYLineAnnotation
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun

class ScatterChartViewModel extends ChartViewModel {
    protected List<Double> means
    protected List<Double> stdDevs

    public ScatterChartViewModel(String title, SimulationRun simulationRun, List nodes) {
        super(title, simulationRun, nodes, 0.0)
        chartProperties = new ChartProperties(title: title, xAxisTitle: null, yAxisTitle: null, showLegend: true)
    }

    public JFreeChart getChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        if (series.size() != 2 || series[0].size() != series[1].size()) {
            return chartInsetWriter.createErrorMessageChart("series contain different number of observations")
        }
//        if (!onlyStochasticSeries) {
        //            return chartInsetWriter.createErrorMessageChart("at least one series is constant")
        //        }

        int seriesCount = 0
        periodCount.times {int periodIndex ->
            if (showLine[0, periodIndex]) {
                XYSeries plotSeries = new XYSeries(getPeriodLabel(periodIndex));
                series[0][periodIndex].eachWithIndex {double v1, int index -> plotSeries.add(v1, (double) series[1][periodIndex][index])}
                dataset.addSeries plotSeries
                seriesCount++
            }
        }
        if (chartProperties.xAxisTitle == null) chartProperties.xAxisTitle = seriesNames[0]
        if (chartProperties.yAxisTitle == null) chartProperties.yAxisTitle = seriesNames[1]
        JFreeChart chart = ChartFactory.createScatterPlot(chartProperties.title, chartProperties.xAxisTitle, chartProperties.yAxisTitle, dataset, PlotOrientation.VERTICAL, chartProperties.showLegend, false, false)
        int seriesIndex = 0
        periodCount.times {int periodIndex ->
            if (showLine[0, periodIndex]) {
                chart.getXYPlot().getRenderer(0).setSeriesPaint seriesIndex, seriesColor.getColorByParam(periodIndex)
                chart.getXYPlot().getRenderer(0).setSeriesShape seriesIndex, new Rectangle(2, 2)
                seriesIndex++
            }
//        addStatisticalValues(chart, seriesColorList[0])
        }
        return chart
    }

    Map getDataTable() {
        Map columns = [:]

        List period = []
        List values0 = []
        List values1 = []

        series[0].eachWithIndex {def periodValues, int periodIndex ->
            if (showLine[0, periodIndex]) {
                periodValues.eachWithIndex {double value, int iterationIndex ->
                    period << getPeriodLabel(periodIndex)
                    values0 << value
                    values1 << series[1][periodIndex][iterationIndex]
                }
            }
        }

        columns["period"] = period
        columns[seriesNames[0]] = values0
        columns[seriesNames[1]] = values1

        return columns
    }

    protected void loadData() {
        means = []
        stdDevs = []

        nodes.each {def node ->
            List periods = []
            List meansP = []
            List stdDevsP = []
            periodCount.times {int periodIndex ->
                onlyStochasticSeries = onlyStochasticSeries && ResultAccessor.hasDifferentValues(simulationRun, periodIndex, node.path, node.collector, node.field)
                periods << ResultAccessor.getValues(simulationRun, periodIndex, node.path, node.collector, node.field)
                stdDevsP << ResultAccessor.getStdDev(simulationRun, periodIndex, node.path, node.collector, node.field)
                meansP << ResultAccessor.getMean(simulationRun, periodIndex, node.path, node.collector, node.field)
            }
            series << periods
            seriesNames << node.getShortDisplayPath(nodes)
            means << meansP
            stdDevs << stdDevsP
        }
    }

    protected void addStatisticalValues(JFreeChart chart, Color c) {
        Color meanColor = new Color((int) (c.getRed() * 0.7),
                (int) (c.getGreen() * 0.7),
                (int) (c.getBlue() * 0.7))

        Color stdDevColor = new Color((int) (c.getRed() * 0.7),
                (int) (c.getGreen() * 0.7),
                (int) (c.getBlue() * 0.7))

        double yMin = chart.getXYPlot().getRangeAxis().getRange().lowerBound
        double yRange = chart.getXYPlot().getRangeAxis().getRange().upperBound - yMin

        XYLineAnnotation meanLine = new XYLineAnnotation(means[0], yMin, means[0], yRange / 20 + yMin, new BasicStroke(1.0f), meanColor)
        chart.getXYPlot().addAnnotation(meanLine)

        XYLineAnnotation stdDevLine = new XYLineAnnotation(means[0] - stdDevs[0], yRange / 40 + yMin, means[0] + stdDevs[0], yRange / 40 + yMin, new BasicStroke(1.0f), stdDevColor)
        chart.getXYPlot().addAnnotation(stdDevLine)

        XYLineAnnotation stdDevLowerLineEnd = new XYLineAnnotation(means[0] - stdDevs[0], yRange * 3 / 80 + yMin, means[0] - stdDevs[0], yRange / 80 + yMin, new BasicStroke(1.0f), stdDevColor)
        chart.getXYPlot().addAnnotation(stdDevLowerLineEnd)

        XYLineAnnotation stdDevUpperLineEnd = new XYLineAnnotation(means[0] + stdDevs[0], yRange * 3 / 80 + yMin, means[0] + stdDevs[0], yRange / 80 + yMin, new BasicStroke(1.0f), stdDevColor)
        chart.getXYPlot().addAnnotation(stdDevUpperLineEnd)


        double xMin = chart.getXYPlot().getDomainAxis().getRange().lowerBound
        double xRange = chart.getXYPlot().getDomainAxis().getRange().upperBound - xMin

        meanLine = new XYLineAnnotation(xMin, means[1], xRange / 20 + xMin, means[1], new BasicStroke(1.0f), meanColor)
        chart.getXYPlot().addAnnotation(meanLine)

        stdDevLine = new XYLineAnnotation(xRange / 40 + xMin, means[1] - stdDevs[1], xRange / 40 + xMin, means[1] + stdDevs[1], new BasicStroke(1.0f), stdDevColor)
        chart.getXYPlot().addAnnotation(stdDevLine)

        stdDevLowerLineEnd = new XYLineAnnotation(xRange * 3 / 80 + xMin, means[1] - stdDevs[1], xRange / 80 + xMin, means[1] - stdDevs[1], new BasicStroke(1.0f), stdDevColor)
        chart.getXYPlot().addAnnotation(stdDevLowerLineEnd)

        stdDevUpperLineEnd = new XYLineAnnotation(xRange * 3 / 80 + xMin, means[1] + stdDevs[1], xRange / 80 + xMin, means[1] + stdDevs[1], new BasicStroke(1.0f), stdDevColor)
        chart.getXYPlot().addAnnotation(stdDevUpperLineEnd)
    }
}