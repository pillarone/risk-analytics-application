package org.pillarone.riskanalytics.application.ui.chart.model

import org.jfree.data.xy.XYSeries
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode

import org.pillarone.riskanalytics.application.util.JEstimator

/**
 *
 *
 * @author martin.melchior (at) fhnw.ch
 */
abstract class AdaptiveKernelBandwidthEstimatorChartViewModel extends KernelEstimatorChartViewModel {

    protected static double CONST1 = 0.7764 * 1.36374
    protected static double threshold = 4.0  //used for speeding up pdf calculations for graphical representations.

    protected double priorBandwidth0
    protected double priorBandwidth

    public AdaptiveKernelBandwidthEstimatorChartViewModel(String title, SimulationRun simulationRun, List<SimpleTableTreeNode> nodes, double insetHeight) {
        super(title, simulationRun, nodes, insetHeight)
    }

    protected XYSeries createSeries(String legendTitle) {
        XYSeries newSeries = new XYSeries(legendTitle)
        addToSeries(newSeries)

        return newSeries
    }

    protected void loadData() {
        super.loadData()
        int n = series[0].size()
        // prior density used to compute the local bandwidths
        // use the 'best' bandwidth for the Gaussian kernel estimator
        if (priorBandwidth0 == 0) {
            priorBandwidth0 = JEstimator.calcBandwidthForGaussKernelEstimate((double) stdDevs[0][0], (double) IQRs[0][0], n)
            priorBandwidth = priorBandwidth0
        }
    }

    protected void storeInCache(int keyFigureIndex, int periodIndex, def series) {
        seriesCache[keyFigureIndex, periodIndex, priorBandwidth0] = series
    }

    protected def getFromCache(int keyFigureIndex, int periodIndex) {
        return seriesCache[keyFigureIndex, periodIndex, priorBandwidth0]
    }

    protected void initializeParameters() {
        observations.sort()
        int n = observations.size()

        // prior density used to compute the local bandwidths
        // use the 'best' bandwidth for the Gaussian kernel estimator
        if (priorBandwidth0 == 0) {
            priorBandwidth0 = JEstimator.calcBandwidthForGaussKernelEstimate(stdDev, IQR, n)
        }
        priorBandwidth = priorBandwidth0
    }
}