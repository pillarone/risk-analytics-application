package org.pillarone.riskanalytics.application.ui.chart.model

import org.jfree.chart.JFreeChart
import org.jfree.data.xy.XYSeries
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.ChartInsetWriter
import org.pillarone.riskanalytics.application.util.JEstimator
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun

/**
 * @author martin.melchior (at) fhnw.ch
 *
 */
class CDFAdaptiveKernelBandwidthEstimatorChartViewModel extends AdaptiveKernelBandwidthEstimatorChartViewModel {

    public CDFGaussKernelEstimateChartViewModel() {}

    public CDFAdaptiveKernelBandwidthEstimatorChartViewModel(String title, SimulationRun simulationRun, List<ResultTableTreeNode> nodes) {
        super(title, simulationRun, nodes, 0.5)
    }

    protected void addHistogram(JFreeChart chart, String legendTitle, double lowerBound, double upperBound) {
        chart.XYPlot.rangeAxis.standardTickUnits = new CDFTickUnit()
    }

    protected void addToSeries(XYSeries seriesCDF) {
        initializeParameters()
        List cdfValues = JEstimator.adaptiveKernelBandwidthCdf(observations, priorBandwidth, false)
        cdfValues.each {List XYPair ->
            seriesCDF.add(XYPair[0], XYPair[1])
        }
    }

    protected void writeInsetContent(ChartInsetWriter writer) {
        writer.writeInset("Prior kernel bandwidth", priorBandwidth)
        writer.writeEmptyLine()
        writer.writePercentiles([1], simulationRun, 0, nodes[0])
        double median = ResultAccessor.getPercentile(simulationRun, nodes[0].path, nodes[0].collector, nodes[0].field, 50)
        writer.writeInset("median", median)
        writer.writeInset("mean", mean)
        writer.writePercentiles([99], simulationRun, 0, nodes[0])
        writer.writeEmptyLine()
        writer.writeInset("stdev", stdDev)
        writer.writeInset("IQR", IQR)
    }

    String getYAxisLabel() { return "probability" }
}
