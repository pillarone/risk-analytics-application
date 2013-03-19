package org.pillarone.riskanalytics.application.ui.chart.model

import com.ulcjava.base.application.ULCSpinnerNumberModel

import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYBarRenderer
import org.jfree.data.statistics.HistogramType
import org.jfree.data.xy.XYSeries
import org.pillarone.riskanalytics.core.output.SimulationRun

import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.ChartInsetWriter
import org.pillarone.riskanalytics.application.util.JEstimator
import org.pillarone.riskanalytics.application.util.MeshCalculations
import org.jfree.data.statistics.HistogramDataset

/**
 * @author fouad jaada
 */

public class PDFRawChartViewModel extends RawChartViewModel {

    public PDFRawChartViewModel() {
    }

    public PDFRawChartViewModel(String title, SimulationRun simulationRun, List<ResultTableTreeNode> nodes) {
        super(title, simulationRun, nodes, 0.8)
    }



    protected void addHistogram(JFreeChart chart, String legendTitle, double lowerBound, double upperBound) {
        XYPlot plot = (XYPlot) chart.getPlot()
        HistogramDataset data = new HistogramDataset()
        data.setType HistogramType.SCALE_AREA_TO_1

        int upperBinCount = Math.min((observations.size() / 5) as int, MeshCalculations.SAMPLE_COUNT)
        int binCount = Math.max(5, upperBinCount)

        data.addSeries(legendTitle, observations as double[], binCount, lowerBound, upperBound)

        XYBarRenderer histogramBarRenderer = new XYBarRenderer()
        histogramBarRenderer.setSeriesPaint 0, new java.awt.Color(230, 230, 230)
        histogramBarRenderer.setSeriesVisibleInLegend 0, false

        plot.setDataset(1, data)
        plot.setRenderer(1, histogramBarRenderer)


    }

    public JFreeChart getChart() {
        JFreeChart chart = super.getChart()

        //sca: JFreeChart bug. See addToSeries
        NumberAxis rangeAxis = chart?.getXYPlot()?.getRangeAxis()
        rangeAxis?.setTickLabelsVisible(false)
        rangeAxis?.setTickMarksVisible(false)

        return chart
    }

    protected void addToSeries(XYSeries seriesPDF) {
        List pdfValues = JEstimator.getPDF(simulationRun, nodes[rawNodeIndex], rawPeriodIndex)
        pdfValues.each {List xyPair ->
            seriesPDF.add(xyPair[0], xyPair[1])
        }
    }

    protected void writeInsetContent(ChartInsetWriter writer) {
        writer.writeInset("Mean", mean)
        writer.writeInset("Stdev", stdDev)
    }

    public ULCSpinnerNumberModel getSpinnerModel() {
        double t0r = Math.round(t0 * 10) / 10
        def stepSize = Math.round(t0r * spinnerStepSize * 10) / 10
        return new ULCSpinnerNumberModel(t0r, 0, maxs.collect {it.max()}.max() - mins.collect { it.min()}.min(), stepSize)
    }

    public void setBandwith(double newT) {
        t0 = newT
        fireModelChanged()
    }

    String getYAxisLabel() { "1 / value" }
}