package org.pillarone.riskanalytics.application.ui.chart.model

import java.awt.Color
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.title.LegendTitle
import org.jfree.data.statistics.HistogramDataset
import org.jfree.ui.HorizontalAlignment
import org.jfree.ui.RectangleEdge
import org.pillarone.riskanalytics.application.dataaccess.function.ResultFunction
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.chart.model.ChartProperties
import org.pillarone.riskanalytics.application.ui.chart.model.ChartViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor

class HistogramChartViewModel extends ChartViewModel {

    int currentBinCount = Math.min(series[0][0].size() / 5, 50)
    static final int minBinCount = 1
    final int maxBinCount = 200
    double binSize

    List<Double> mins
    List<Double> maxs
    double min, max

    public HistogramChartViewModel() {}

    public HistogramChartViewModel(String title, SimulationRun simulationRun, List nodes) {
        super(title, simulationRun, nodes, 0.0)
        chartProperties = new ChartProperties(title: title, xAxisTitle: 'value', yAxisTitle: 'count', showLegend: true)
    }

    public JFreeChart getChart() {
        if (!onlyStochasticSeries) {
            return chartInsetWriter.createErrorMessageChart("at least one series is constant")
        }

        HistogramDataset data = new HistogramDataset()

        series.eachWithIndex {List observations, int keyFigureIndex ->
            observations.eachWithIndex {List<Double> periods, int periodIndex ->
                if (showLine[keyFigureIndex, periodIndex]) {
                    data.addSeries("${seriesNames[keyFigureIndex]} P${getPeriodLabel(periodIndex)}", periods as double[], currentBinCount, min, max)   // Math.floor((max-min)/binSize)
                }
            }
        }

        JFreeChart chart = ChartFactory.createHistogram(chartProperties.title, chartProperties.xAxisTitle, chartProperties.yAxisTitle, data, PlotOrientation.VERTICAL, chartProperties.showLegend, false, false)

        //set series colors
        if (series.size() > 1 && series.size() <= seriesColorList.size()) {
            for (index in 0..<series.size()) {
                Color c = seriesColorList[index]
                Color tc = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255 / series.size()))
                chart.getXYPlot().getRenderer(0).setSeriesPaint(index, tc)
            }
        }

        chart.getXYPlot().rangeAxis.standardTickUnits = NumberAxis.createIntegerTickUnits()

        if (chartProperties.showLegend) {
            //set legend position
            LegendTitle legend = chart.getLegend();
            legend.setPosition(RectangleEdge.BOTTOM);
            legend.setHorizontalAlignment(HorizontalAlignment.LEFT);
            legend.setMargin 5, 50, 5, 5
        } else {
            chart.removeLegend()
        }

        return chart
    }

    Map getDataTable() {
        HistogramDataset data = new HistogramDataset()
        Map columns = [:]

        series.eachWithIndex {observations, keyFigureIndex ->
            observations.eachWithIndex {periods, periodIndex ->
                if (showLine[keyFigureIndex, periodIndex]) {
                    String seriesName = "${seriesNames[keyFigureIndex]} for ${getPeriodLabel(periodIndex)}"
                    data.addSeries(seriesName, periods as double[], currentBinCount, min, max)   // Math.floor((max-min)/binSize)
                }
            }
        }

        List lowerBinBoundery = []
        List upperBinBoundery = []

        data.getItemCount(0).times {int binIndex ->
            lowerBinBoundery << data.getStartX(0, binIndex)
            upperBinBoundery << data.getEndX(0, binIndex)
        }

        columns["lower bin boundery"] = lowerBinBoundery
        columns["upper bin boundery"] = upperBinBoundery

        data.getSeriesCount().times {int seriesIndex ->
            List column = []
            data.getItemCount(seriesIndex).times {int binIndex ->
                column << data.getY(seriesIndex, binIndex)
            }
            columns[data.getSeriesKey(seriesIndex)] = column
        }

        return columns
    }

    void setCurrentBinCount(int newBinCount) {
        this.currentBinCount = newBinCount
        fireModelChanged()
    }

    protected void loadData() {
        mins = []
        maxs = []

        nodes.each {ResultTableTreeNode node ->
            List periods = []
            List minsP = []
            List maxsP = []
            periodCount.times {int periodIndex ->
                onlyStochasticSeries = onlyStochasticSeries && ResultAccessor.hasDifferentValues(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)
                periods << ResultAccessor.getValues(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)
                minsP << ResultAccessor.getMin(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)
                maxsP << ResultAccessor.getMax(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)
            }
            series << periods
            seriesNames << node.getShortDisplayPath(nodes)
            mins << minsP
            maxs << maxsP
        }


        max = 0
        min = Double.MAX_VALUE

        maxs.eachWithIndex {m, index ->
            m.eachWithIndex {m2, index2 ->
                max = Math.max(max, m2)
                min = Math.min(min, mins[index][index2])
            }
        }
    }


}