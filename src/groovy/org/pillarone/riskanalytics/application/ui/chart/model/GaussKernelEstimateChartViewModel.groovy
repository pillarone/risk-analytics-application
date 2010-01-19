package org.pillarone.riskanalytics.application.ui.chart.model

import org.jfree.data.xy.XYSeries
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode

import org.pillarone.riskanalytics.application.util.JEstimator

abstract class GaussKernelEstimateChartViewModel extends KernelEstimatorChartViewModel {

    double scalingFactor
    double t0  //initial calculated bandwidth
    /* The parameter t has the same dimension as the data (and e.g. the standard deviation)
    - and not, as described in many papers, the dimension of its square (e.g. like the variance).  */
    double t    //may be changed by ui


    public GaussKernelEstimateChartViewModel() {}

    public GaussKernelEstimateChartViewModel(String title, SimulationRun simulationRun, List<SimpleTableTreeNode> nodes, double insetHeight) {
        super(title, simulationRun, nodes, insetHeight)

    }

    protected XYSeries createSeries(String legendTitle) {

        XYSeries newSeries = new XYSeries(legendTitle);
        addToSeries(newSeries)
        return newSeries
    }

    protected void storeInCache(int keyFigureIndex, int periodIndex, def series) {
        seriesCache[keyFigureIndex, periodIndex, t0] = series
    }

    protected def getFromCache(int keyFigureIndex, int periodIndex) {
        return seriesCache[keyFigureIndex, periodIndex, t0]
    }

    protected void loadData() {
        super.loadData()
        int n = series[0].size()
        // prior density used to compute the local bandwidths
        // use the 'best' bandwidth for the Gaussian kernel estimator
        if (t0 == 0) {
            t0 = JEstimator.calcBandwidthForGaussKernelEstimate((double) stdDevs[0][0], (double) IQRs[0][0], n)
            t = t0
        }
    }

    protected void initializeParameters() {
        observations.sort()
        double n = observations.size()

        // prior density used to compute the local bandwidths
        // use the 'best' bandwidth for the Gaussian kernel estimator
        if (t0 == 0) {
            t0 = JEstimator.calcBandwidthForGaussKernelEstimate(stdDev, IQR, n)
        }
        t = t0
    }
}