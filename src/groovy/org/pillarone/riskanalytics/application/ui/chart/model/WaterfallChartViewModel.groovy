package org.pillarone.riskanalytics.application.ui.chart.model

import java.awt.Color
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.category.BarRenderer
import org.jfree.chart.renderer.category.WaterfallBarRenderer
import org.jfree.data.category.DefaultCategoryDataset
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun

public class WaterfallChartViewModel extends ChartViewModel {

    List periodValues
    int currentPeriod = 0

    public static int VAR_FUNCTION = 1
    public static int TVAR_FUNCTION = 2

    int function = VAR_FUNCTION
    double percent = 99.5
    double diversification = 0

    public WaterfallChartViewModel(String title, SimulationRun simulationRun, List<SimpleTableTreeNode> nodes) {
        super(title, simulationRun, nodes, 0.8)
        chartProperties = new ChartProperties(title: 'Waterfall Chart', xAxisTitle: '', yAxisTitle: '', showLegend: false)
    }

    public JFreeChart getChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        double sum = 0
        (periodValues.size() - 1).times {int index ->
            double value
            if (function == VAR_FUNCTION) {
                value = ResultAccessor.getVar(simulationRun, currentPeriod, nodes[index].path, nodes[index].collector, nodes[index].field, percent)
            } else {
                value = ResultAccessor.getTvar(simulationRun, currentPeriod, nodes[index].path, nodes[index].collector, nodes[index].field, percent)
            }

            dataset.addValue(value, "", seriesNames[index]);
            sum += value
        }

        double lastValue
        if (function == VAR_FUNCTION) {
            lastValue = ResultAccessor.getVar(simulationRun, currentPeriod, nodes[-1].path, nodes[-1].collector, nodes[-1].field, percent)
        } else {
            lastValue = ResultAccessor.getTvar(simulationRun, currentPeriod, nodes[-1].path, nodes[-1].collector, nodes[-1].field, percent)
        }
        diversification = lastValue - sum
        dataset.addValue(diversification, "", "diversification")
        dataset.addValue(lastValue, "", seriesNames[-1])

        JFreeChart chart = ChartFactory.createWaterfallChart(
                chartProperties.title,
                chartProperties.xAxisTitle,
                chartProperties.yAxisTitle,
                dataset,
                PlotOrientation.VERTICAL,
                chartProperties.showLegend,
                true,
                false
        )

        WaterfallBarRenderer renderer = (BarRenderer) chart.getPlot().getRenderer();
        renderer.firstBarPaint = new Color(247, 139, 0)
        renderer.positiveBarPaint = new Color(247, 139, 0)
        renderer.negativeBarPaint = new Color(173, 228, 142)
        renderer.lastBarPaint = new Color(247, 139, 0)

        return chart
    }

    protected void loadData() {
        periodValues = []
        nodes.each {SimpleTableTreeNode node ->
            List values = []
            periodCount.times {int periodIndex ->
                values << ResultAccessor.getMean(simulationRun, periodIndex, node.path, node.collector, node.field)
            }
            periodValues << values
            seriesNames << node.getShortDisplayPath(nodes)
        }
    }

    public moveSeriesUp(int seriesIndex) {
        if (seriesIndex < 1) { return }
        def oSeries = periodValues[seriesIndex - 1]
        String oSeriesName = seriesNames[seriesIndex - 1]
        def oNode = nodes[seriesIndex - 1]

        periodValues[seriesIndex - 1] = periodValues[seriesIndex]
        seriesNames[seriesIndex - 1] = seriesNames[seriesIndex]
        nodes[seriesIndex - 1] = nodes[seriesIndex]

        periodValues[seriesIndex] = oSeries
        seriesNames[seriesIndex] = oSeriesName
        nodes[seriesIndex] = oNode

        fireModelChanged()
    }

    public moveSeriesDown(int seriesIndex) {
        if (seriesIndex >= (seriesNames.size() - 1)) { return }
        def oSeries = periodValues[seriesIndex + 1]
        String oSeriesName = seriesNames[seriesIndex + 1]
        def oNode = nodes[seriesIndex + 1]

        periodValues[seriesIndex + 1] = periodValues[seriesIndex]
        seriesNames[seriesIndex + 1] = seriesNames[seriesIndex]
        nodes[seriesIndex + 1] = nodes[seriesIndex]

        periodValues[seriesIndex] = oSeries
        seriesNames[seriesIndex] = oSeriesName
        nodes[seriesIndex] = oNode

        fireModelChanged()
    }

    public void setPeriodVisibility(int period, boolean value) {
        if (value) {
            currentPeriod = period
            fireModelChanged()
        }
    }

    Map getDataTable() {
        Map m = [:]
        m[""] = []
        String columnHeader
        if (function == VAR_FUNCTION) {
            columnHeader = "VaR " + percent
        } else {
            columnHeader = "VaR " + percent
        }
        m[columnHeader] = []
        seriesNames.eachWithIndex {String seriesName, int nameIndex ->
            m[""] << seriesName + " for " + getPeriodLabel(currentPeriod)
            double value
            if (function == VAR_FUNCTION) {
                value = ResultAccessor.getVar(simulationRun, currentPeriod, nodes[nameIndex].path, nodes[nameIndex].collector, nodes[nameIndex].field, percent)
            } else {
                value = ResultAccessor.getTvar(simulationRun, currentPeriod, nodes[nameIndex].path, nodes[nameIndex].collector, nodes[nameIndex].field, percent)
            }
            m[columnHeader] << value
        }
        m[""] << "diversification"
        m[columnHeader] << diversification

        return m
    }

    public void setFunction(int function, double percentile) {
        this.function = function
        this.percent = percentile
        fireModelChanged()
    }
}