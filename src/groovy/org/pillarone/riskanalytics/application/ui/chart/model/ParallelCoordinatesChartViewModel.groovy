package org.pillarone.riskanalytics.application.ui.chart.model

import java.awt.BasicStroke
import java.awt.Color
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.annotations.CategoryLineAnnotation
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.AbstractRenderer
import org.jfree.data.category.DefaultCategoryDataset
import org.pillarone.riskanalytics.application.dataaccess.function.ResultFunction
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun

public class ParallelCoordinatesChartViewModel extends ChartViewModel implements IModelChangedListener {
    double min, max
    List mins
    List maxs
    QueryPaneModel queryPaneModel
    int currentPeriod = 0

    public ParallelCoordinatesChartViewModel(String title, SimulationRun simulationRun, List<SimpleTableTreeNode> nodes, boolean showPeriodLabels = true) {
        super(title, simulationRun, nodes, 0.8)
        queryPaneModel = new ParallelCoordinatesQueryPaneModel(simulationRun, nodes, false, showPeriodLabels)
        queryPaneModel.addModelChangedListener this
        chartProperties = new ChartProperties(title: 'Parallel Coordinates Plot', xAxisTitle: '', yAxisTitle: '', showLegend: true)
        initData()
    }

    public JFreeChart getChart() {

        DefaultCategoryDataset currentDataset = dataset

        JFreeChart chart = ChartFactory.createLineChart(
                chartProperties.title,      // chart title
                chartProperties.xAxisTitle,                      // x axis label
                chartProperties.yAxisTitle,                      // y axis label
                currentDataset,                  // data
                PlotOrientation.VERTICAL,
                chartProperties.showLegend,                     // include legend
                true,                     // tooltips
                false                     // urls
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        addAnnotations plot

        calculateRainbowColors(plot.getRenderer(), currentDataset)

        chart.removeLegend()
        return chart;
    }

    public DefaultCategoryDataset getDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List resultValues = queryPaneModel.createResultList()
        resultValues.each {List<Integer> row ->
            int iterationNumber = row[0]
            int numberOfPaths = series.size()
            int start = 1 + (currentPeriod * numberOfPaths)
            int end = ((1 + currentPeriod) * numberOfPaths)
            (start..end).each {
                dataset.addValue(row[it], iterationNumber, seriesNames[it - start])
            }
        }

        return dataset;
    }

    public void addAnnotations(CategoryPlot plot) {
        seriesNames.each {String name ->
            plot.addAnnotation(new CategoryLineAnnotation(name, 0, name, max * 1.1, Color.gray, new BasicStroke(1.0f)))
        }
    }

    protected void calculateRainbowColors(AbstractRenderer renderer, DefaultCategoryDataset dataset) {
        List rowKeys = dataset.getRowKeys()
        int i = 0;
        rowKeys.each {int rowIndex ->
            Color color
            Double value = dataset.getValue(rowIndex, seriesNames[-1])
            int g = 255.0
            if (value)
                g = 255.0 * (maxs[-1][currentPeriod] - value) / (maxs[-1][currentPeriod] - mins[-1][currentPeriod])
            int r = 255 - g

            color = new Color(r, g, 0)
            renderer.setSeriesPaint(i, color)
            i++
        }
    }

    protected List createFoundResultList() {
        return queryPaneModel.createResultList().collect { it[0] }
    }

    Map getDataTable() {
        Map columns = [:]
        DefaultCategoryDataset dataset = getDataset()

        List period = []
        dataset.rowCount.times { period << currentPeriod}
        columns["period"] = period

        dataset.columnKeys.each {String columnKey ->
            columns[columnKey] = []
            dataset.rowKeys.each {int rowKey ->
                columns[columnKey] << dataset.getValue(rowKey, columnKey)
            }
        }
        return columns
    }

    public moveSeriesUp(int seriesIndex) {
        if (seriesIndex < 1) { return }
        def oSeries = series[seriesIndex - 1]
        String oSeriesName = seriesNames[seriesIndex - 1]
        def oMax = maxs[seriesIndex - 1]
        def oMin = mins[seriesIndex - 1]
        SimpleTableTreeNode oNode = nodes[seriesIndex - 1]
        def oNode2 = queryPaneModel.nodes[seriesIndex - 1]

        series[seriesIndex - 1] = series[seriesIndex]
        seriesNames[seriesIndex - 1] = seriesNames[seriesIndex]
        maxs[seriesIndex - 1] = maxs[seriesIndex]
        mins[seriesIndex - 1] = mins[seriesIndex]
        nodes[seriesIndex - 1] = nodes[seriesIndex]
        queryPaneModel.nodes[seriesIndex - 1] = queryPaneModel.nodes[seriesIndex]

        series[seriesIndex] = oSeries
        seriesNames[seriesIndex] = oSeriesName
        maxs[seriesIndex] = oMax
        mins[seriesIndex] = oMin
        nodes[seriesIndex] = oNode
        queryPaneModel.nodes[seriesIndex] = oNode2

        queryPaneModel.query()
        fireModelChanged()
    }

    public moveSeriesDown(int seriesIndex) {
        if (seriesIndex >= (series.size() - 1)) { return }
        def oSeries = series[seriesIndex + 1]
        String oSeriesName = seriesNames[seriesIndex + 1]
        def oMax = maxs[seriesIndex + 1]
        def oMin = mins[seriesIndex + 1]
        SimpleTableTreeNode oNode = nodes[seriesIndex + 1]
        def oNode2 = queryPaneModel.nodes[seriesIndex + 1]

        series[seriesIndex + 1] = series[seriesIndex]
        seriesNames[seriesIndex + 1] = seriesNames[seriesIndex]
        maxs[seriesIndex + 1] = maxs[seriesIndex]
        mins[seriesIndex + 1] = mins[seriesIndex]
        nodes[seriesIndex + 1] = nodes[seriesIndex]
        queryPaneModel.nodes[seriesIndex + 1] = queryPaneModel.nodes[seriesIndex]

        series[seriesIndex] = oSeries
        seriesNames[seriesIndex] = oSeriesName
        maxs[seriesIndex] = oMax
        mins[seriesIndex] = oMin
        nodes[seriesIndex] = oNode
        queryPaneModel.nodes[seriesIndex] = oNode2

        queryPaneModel.query()
        fireModelChanged()
    }

    /*
    Cannot load data here, because the order of nodes is changed in the constructor of this class and this method is called earlier.
     */

    protected void loadData() {
    }

    protected void initData() {
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

    public void modelChanged() {
        fireModelChanged()
    }

    public void setPeriodVisibility(int period, boolean value) {
        if (value) {
            currentPeriod = period
            queryPaneModel.defaultPeriod = currentPeriod
            queryPaneModel.query()
            fireModelChanged()
        }
    }

    public boolean isChangeColorEnabled() {
        return false;
    }


}