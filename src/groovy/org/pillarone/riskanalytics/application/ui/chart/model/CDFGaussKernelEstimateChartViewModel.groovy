package org.pillarone.riskanalytics.application.ui.chart.model

import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberTickUnit
import org.jfree.chart.axis.TickUnit
import org.jfree.chart.axis.TickUnitSource
import org.jfree.data.xy.XYSeries
import org.pillarone.riskanalytics.application.dataaccess.function.PercentileFunction
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.ChartInsetWriter
import org.pillarone.riskanalytics.application.util.JEstimator
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.core.output.SimulationRun

class CDFGaussKernelEstimateChartViewModel extends GaussKernelEstimateChartViewModel {

    public CDFGaussKernelEstimateChartViewModel(String title, SimulationRun simulationRun, List<ResultTableTreeNode> nodes) {
        super(title, simulationRun, nodes, 0.5)
    }

    protected void addHistogram(JFreeChart chart, String legendTitle, double lowerBound, double upperBound) {
        chart.XYPlot.rangeAxis.standardTickUnits = new CDFTickUnit()
    }


    protected void addToSeries(XYSeries seriesCDF) {
        initializeParameters()
        List cdfValues = JEstimator.gaussKernelBandwidthCdf(observations, t, false)
        cdfValues.each {List xyPair ->
            seriesCDF.add(xyPair[0], xyPair[1])
        }
    }

    protected void writeInsetContent(ChartInsetWriter writer) {
        writer.writeInset("Kernel bandwidth", t)
        writer.writeEmptyLine()
        writer.writePercentiles([1], simulationRun, 0, nodes[0])
        PercentileFunction percentile = new PercentileFunction(50, QuantilePerspective.LOSS)
        double median = percentile.evaluate(simulationRun, 0, nodes[0])
        writer.writeInset("median", median)
        writer.writeInset("mean", mean)
        writer.writePercentiles([99], simulationRun, 0, nodes[0])
        writer.writeEmptyLine()
        writer.writeInset("stdev", stdDev)
        writer.writeInset("IQR", IQR)
    }

    String getYAxisLabel() { return "probability" }

}

class CDFTickUnit implements TickUnitSource {
    static double tickDistance = 0.2

    public TickUnit getLargerTickUnit(TickUnit tickUnit) {
        return new NumberTickUnit(tickUnit.getSize() + tickDistance)
    }

    public TickUnit getCeilingTickUnit(TickUnit tickUnit) {
        return new NumberTickUnit(tickUnit.getSize() + tickDistance)
    }

    public TickUnit getCeilingTickUnit(double v) {
        return new NumberTickUnit(Math.round(v / tickDistance) * tickDistance)
    }

}