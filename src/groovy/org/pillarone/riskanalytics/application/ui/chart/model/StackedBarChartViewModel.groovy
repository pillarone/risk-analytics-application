package org.pillarone.riskanalytics.application.ui.chart.model

import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun
import java.awt.Color

public class StackedBarChartViewModel extends ChartViewModel {
    List means

    public StackedBarChartViewModel(String title, SimulationRun simulationRun, List<SimpleTableTreeNode> nodes) {
        super(title, simulationRun, nodes, 0.8)
        chartProperties = new ChartProperties(title: title, xAxisTitle: '', yAxisTitle: '', showLegend: true)
        selectAll(true)
    }


    public JFreeChart getChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        JFreeChart chart = ChartFactory.createStackedBarChart(
                chartProperties.title,         // chart title
                chartProperties.xAxisTitle,               // domain axis label
                chartProperties.yAxisTitle,                  // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                chartProperties.showLegend,                     // include legend
                true,                     // tooltips?
                false                     // URLs?
        );
        setBackground(chart, Color.white)

        means.eachWithIndex {List series, int seriesIndex ->
            series.eachWithIndex {double values, int periodIndex ->
                if (showLine[seriesIndex, periodIndex]) {
                    dataset.addValue(values, seriesNames[seriesIndex], getPeriodLabel(periodIndex))
                    chart.getPlot().getRenderer(0).setSeriesPaint seriesIndex, seriesColor.getColorByParam(seriesIndex)
                }
            }
        }

        return chart
    }

    protected void loadData() {
        means = []

        nodes.each {ResultTableTreeNode node ->
            List meansP = []
            periodCount.times {int periodIndex ->
                meansP << ResultAccessor.getMean(simulationRun, periodIndex, node.path, node.collector, node.field)
            }
            seriesNames << node.getShortDisplayPath(nodes)
            means << meansP
        }
    }

    Map getDataTable() {
        if (periodCount > 254) {
            throw new UnsupportedOperationException("You can not export more than 254 periods. Excel supports only 255 columns.")
        }
        dataExportMode = true
        Map columns = [:]

        columns["key figure"] = []
        seriesNames.eachWithIndex {String keyFigure, int index ->
            if (showLine[index, 0]) {
                columns["key figure"] << keyFigure
            }
        }

        periodCount.times {int periodIndex ->
            columns[getPeriodLabel(periodIndex)] = []
        }

        means.eachWithIndex {List series, int seriesIndex ->
            series.eachWithIndex {double values, int periodIndex ->
                if (showLine[seriesIndex, 0]) {
                    columns[getPeriodLabel(periodIndex)] << values
                }
            }
        }


        dataExportMode = false
        return columns
    }

}