package org.pillarone.riskanalytics.application.ui.chart.model

import groovy.transform.CompileStatic
import org.jfree.chart.JFreeChart
import org.jfree.data.xy.XYSeries
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.ChartInsetWriter
import org.pillarone.riskanalytics.application.util.JEstimator
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.QuantilePerspective

/**
 * @author martin.melchior (at) fhnw.ch
 *
 */
@CompileStatic
class CDFAdaptiveKernelBandwidthEstimatorChartViewModel extends AdaptiveKernelBandwidthEstimatorChartViewModel {

    public CDFGaussKernelEstimateChartViewModel() {}

    public CDFAdaptiveKernelBandwidthEstimatorChartViewModel(String title, SimulationRun simulationRun, List<ResultTableTreeNode> nodes) {
        super(title, simulationRun, nodes as List<SimpleTableTreeNode>, 0.5)
    }

    protected void addHistogram(JFreeChart chart, String legendTitle, double lowerBound, double upperBound) {
        chart.XYPlot.rangeAxis.standardTickUnits = new CDFTickUnit()
    }

    protected void addToSeries(XYSeries seriesCDF) {
        initializeParameters()
        List cdfValues = JEstimator.adaptiveKernelBandwidthCdf(observations, priorBandwidth, false)
        cdfValues.each { List<Double> XYPair ->
            seriesCDF.add(XYPair[0], XYPair[1])
        }
    }

    protected void writeInsetContent(ChartInsetWriter writer) {
        writer.writeInset("Prior kernel bandwidth", priorBandwidth)
        writer.writeEmptyLine()
        ResultTableTreeNode node = nodes[0] as ResultTableTreeNode
        writer.writePercentiles([1], simulationRun, 0, node)
        double median = ResultAccessor.getPercentile(simulationRun, 0, node.path, node.collector, node.field, 50 as Double, QuantilePerspective.LOSS)
        writer.writeInset("median", median)
        writer.writeInset("mean", mean)
        writer.writePercentiles([99], simulationRun, 0, nodes[0])
        writer.writeEmptyLine()
        writer.writeInset("stdev", stdDev)
        writer.writeInset("IQR", IQR)
    }

    String getYAxisLabel() { return "probability" }
}
