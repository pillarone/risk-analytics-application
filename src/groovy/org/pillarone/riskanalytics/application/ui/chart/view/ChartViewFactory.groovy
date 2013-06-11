package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.result.view.ChartType
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.ui.chart.model.*
import org.pillarone.riskanalytics.application.ui.chart.view.*

abstract class ChartViewFactory {

    static ChartView getChart(ChartType chartType, String chartTitle, SimulationRun simulationRun, List<SimpleTableTreeNode> selectedNodes) {
        switch (chartType) {
            case ChartType.HISTOGRAM:
                return new HistogramChartView(new HistogramChartViewModel(chartTitle, simulationRun, selectedNodes))
            case ChartType.DISTRIBUTIONS:
                return new DistributionChartsView(new DistributionChartsViewModel(chartTitle, simulationRun, selectedNodes))
            case ChartType.DiscretePDF:
                return new DiscretePDFChartView(new DiscretePDFChartViewModel(chartTitle, simulationRun, selectedNodes))
            case ChartType.Scatter:
                return new ScatterChartView(new ScatterChartViewModel(chartTitle, simulationRun, selectedNodes))
            case ChartType.PARALLEL_COORDINATES:
                return new ParallelCoordinatesChartView(new ParallelCoordinatesChartViewModel(chartTitle, simulationRun, selectedNodes))
            case ChartType.WATERFALL:
                return new WaterfallChartView(new WaterfallChartViewModel(chartTitle, simulationRun, selectedNodes))
            case ChartType.STACKED_BAR_CHART:
                return new ChartView(new StackedBarChartViewModel(chartTitle, simulationRun, selectedNodes))
            case ChartType.LINE_CHART:
                return new ChartView(new LineChartViewModel(chartTitle, simulationRun, selectedNodes))
        }
    }

    static ULCIcon getChartIcon(ChartType chartType) {
        switch (chartType) {
            case ChartType.HISTOGRAM:
                return UIUtils.getIcon("chartcolumns-active.png")
            case ChartType.DISTRIBUTIONS:
                return UIUtils.getIcon("chartline2-active.png")
            case ChartType.DiscretePDF:
                break;
            case ChartType.Scatter:
                return UIUtils.getIcon("chartpoints-active.png")
            case ChartType.PARALLEL_COORDINATES:
                break;
            case ChartType.WATERFALL:
                return UIUtils.getIcon("chart-waterfall.png")
            case ChartType.STACKED_BAR_CHART:
                return UIUtils.getIcon("chartcolumns-active.png")
            case ChartType.LINE_CHART:
                return UIUtils.getIcon("lineChart.png")
        }
        return UIUtils.getIcon("chartline3-active.png")
    }
}