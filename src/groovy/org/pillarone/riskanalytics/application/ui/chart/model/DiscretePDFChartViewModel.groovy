package org.pillarone.riskanalytics.application.ui.chart.model

import java.awt.BasicStroke
import java.awt.Color
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.xy.XYStepRenderer
import org.jfree.data.statistics.SimpleHistogramBin
import org.jfree.data.statistics.SimpleHistogramDataset
import org.pillarone.riskanalytics.application.ui.util.ChartInsetWriter
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun

class DiscretePDFChartViewModel extends ChartViewModel {
    double mean
    double stdDev
    double min
    double max

    int currentBinSize = 1
    int maxBinSize

    public DiscretePDFChartViewModel(String title, SimulationRun simulationRun, List nodes) {
        super(title, simulationRun, nodes, 0.8)
        chartProperties = new ChartProperties(title: title, xAxisTitle: 'value', yAxisTitle: 'probability', showLegend: true)
    }

    public JFreeChart getChart() {
        if (!onlyStochasticSeries) {
            return chartInsetWriter.createErrorMessageChart("at least one series is non stochastic")
        }
//        HistogramDataset data = new HistogramDataset()
        JFreeChart chart = ChartFactory.createHistogram(chartProperties.title, chartProperties.xAxisTitle, chartProperties.yAxisTitle, null, PlotOrientation.VERTICAL, chartProperties.showLegend, false, false)
        List<SimpleHistogramDataset> sets = []

        series.eachWithIndex {observations, index ->
            SimpleHistogramDataset set = new SimpleHistogramDataset(seriesNames[index])


            int lowerBinBound = min - currentBinSize
            set.addBin new SimpleHistogramBin(lowerBinBound, lowerBinBound + currentBinSize, true, true)
            lowerBinBound += currentBinSize
            while (lowerBinBound <= max) {
                set.addBin new SimpleHistogramBin(lowerBinBound, lowerBinBound + currentBinSize, false, true)
                lowerBinBound += currentBinSize
            }
            set.addBin new SimpleHistogramBin(lowerBinBound, lowerBinBound + currentBinSize, false, true)

            set.addObservations observations as double[]

            sets << set


            XYStepRenderer discreteLineRenderer = new XYStepRenderer()
            discreteLineRenderer.setSeriesStroke 0, new BasicStroke(chartLineThickness)

            Color c = seriesColor.getColorByParam(index)
            Color tc = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255 / series.size()))
            discreteLineRenderer.setSeriesPaint 0, tc


            chart.getXYPlot().setDataset(index, set)
            chart.getXYPlot().setRenderer(index, discreteLineRenderer)
        }


        if (nodes.size() == 1) {
            chartInsetWriter.addInset(chart, {ChartInsetWriter w -> writeInsetContent w})
        }

        if (!chartProperties.showLegend) {
            chart.removeLegend()
        }

        return chart
    }

    protected void writeInsetContent(ChartInsetWriter writer) {
        writer.writeInset("mean", mean)
        writer.writeInset("stdev", stdDev)
    }

    protected void loadData() {
        min = Double.MAX_VALUE
        max = 0

        nodes.each {
            onlyStochasticSeries = onlyStochasticSeries && ResultAccessor.hasDifferentValues(simulationRun, it.path)
            series << ResultAccessor.getValues(simulationRun, it.path)
            seriesNames << it.getDisplayPath()
            min = Math.min(min, ResultAccessor.getMin(simulationRun, it.path))
            max = Math.max(max, ResultAccessor.getMax(simulationRun, it.path))
        }

        maxBinSize = max - min

        if (nodes.size() == 1) {
            mean = ResultAccessor.getMean(simulationRun, nodes[0].path)
            stdDev = ResultAccessor.getStdDev(simulationRun, nodes[0].path)
        }
    }

    void setCurrentBinSize(int newBinSize) {
        this.currentBinSize = newBinSize
        fireModelChanged()
    }
}



