package org.pillarone.riskanalytics.application.ui.chart.model

import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset
import org.pillarone.riskanalytics.application.dataaccess.function.ResultFunction
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun

public class LineChartViewModel extends ChartViewModel {
    List means

    public LineChartViewModel(String title, SimulationRun simulationRun, List nodes) {
        super(title, simulationRun, nodes, 0.0)
        chartProperties = new ChartProperties(title: title, xAxisTitle: '', yAxisTitle: '', showLegend: true)
        selectAll(true)
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

        means.eachWithIndex {List series, int seriesIndex ->
            series.eachWithIndex {double values, int periodIndex ->
                if (showLine[seriesIndex, periodIndex]) {
                    chart.getPlot().getRenderer(0).setSeriesPaint seriesIndex, seriesColor.getColorByParam(seriesIndex)
                }
            }
        }

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        return chart;
    }

    public DefaultCategoryDataset getDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        means.eachWithIndex {List series, int seriesIndex ->
            series.eachWithIndex {double values, int periodIndex ->
                if (showLine[seriesIndex, periodIndex]) {
                    dataset.addValue(values, seriesNames[seriesIndex], getPeriodLabel(periodIndex))
                }
            }
        }

        return dataset
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


    protected void loadData() {
        means = []

        nodes.each {def node ->
            List meansP = []
            periodCount.times {int periodIndex ->
                meansP << ResultAccessor.getMean(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)
            }
            seriesNames << node.getShortDisplayPath(nodes)
            means << meansP
        }
    }
}