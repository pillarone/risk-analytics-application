package org.pillarone.riskanalytics.application.util

import umontreal.iro.lecuyer.probdist.NormalDist

class Estimator {
    public static double CONST = 0.7764 * 1.36374
    protected static double CONST2 = 1d / Math.sqrt(2d * Math.PI)
    protected static double threshold = 4.0  //used for speeding up pdf calculations for graphical representations.

    public static List gaussKernelBandwidthCdf(List<Double> observations, double t, boolean dataExportMode) {
        println "use JEstimator instead of Estimator"
        List cdfValues = []
        List<Double> plotMesh = MeshCalculations.getUniformMeshWithModifiedEdges(t, dataExportMode, observations)
        for (double x: plotMesh) {
            // we start to plot before the min value of the obervations because a continuous pdf starts at value =0
            double y = Estimator._gaussKernelBandwidthCdf(x, observations, t)
            cdfValues << [x, y]
        }
        return cdfValues
    }

    public static List adaptiveKernelBandwidthCdf(List<Double> observations, double priorBandwidth, boolean dataExportMode) {
        println "use JEstimator instead of Estimator"
        List cdfValues = []
        List<Double> plotMesh = MeshCalculations.getUniformMeshWithModifiedEdges(priorBandwidth, dataExportMode, observations)
        List<Integer> meshCounts = MeshCalculations.calculateMeshCount(plotMesh, observations)
        List<Double> bandwidths = computeBandwidths(plotMesh, meshCounts, priorBandwidth, observations, dataExportMode)
        for (double x: plotMesh) {
            // we start to plot before the min value of the obervations because a continuous pdf starts at value =0
            double y = _adaptiveKernelBandwidthCdf(x, priorBandwidth, observations, dataExportMode, plotMesh, meshCounts, bandwidths)
            cdfValues << [x, y]
        }
        return cdfValues
    }

    public static List gaussKernelBandwidthPdf(List<Double> observations, double t, boolean dataExportMode) {
        println "use JEstimator instead of Estimator"
        List pdfValues = []
        List<Double> plotMesh = MeshCalculations.getUniformMeshWithModifiedEdges(t, dataExportMode, observations)
        double norm = 0d
        double meshPoint = plotMesh.get(0)
        double lastMeshPoint = meshPoint
        double lastY = 0
        pdfValues << [meshPoint, lastY]
        for (int i in 1..<plotMesh.size()) {
            // we start to plot before the min value of the obervations because a continuous pdf starts at value =0
            lastMeshPoint = meshPoint
            meshPoint = plotMesh.get(i)
            double y = Estimator._gaussKernelBandwidthPdf(meshPoint, observations, t)
            norm += y * (meshPoint - lastMeshPoint)
            pdfValues << [meshPoint, y]
        }

        if (Math.abs(norm - 1d) > 1e-6) {
            double factor = 1d / norm
            pdfValues.each {List XYPair ->
                XYPair[1] = XYPair[1] * factor
            }
        }
        return pdfValues
    }

    public static List adaptiveKernelBandwidthPdf(List observations, double priorBandwidth, boolean dataExportMode) {
        println "use JEstimator instead of Estimator"
        List pdfValues = []
        List<Double> plotMesh = MeshCalculations.getUniformMeshWithModifiedEdges(priorBandwidth, dataExportMode, observations)
        List<Integer> meshCounts = MeshCalculations.calculateMeshCount(plotMesh, observations)
        List<Double> bandwidths = computeBandwidths(plotMesh, meshCounts, priorBandwidth, observations, dataExportMode)
        double norm = 0d
        double meshPoint = plotMesh.get(0)
        double lastMeshPoint = meshPoint
        double lastY = Estimator._adaptiveKernelBandwidthPdf(meshPoint, priorBandwidth, observations, dataExportMode, plotMesh, meshCounts, bandwidths)  // this mightnot be 0, but setting it hard at some xvalue to zero looks odd, for obs.size large it is ok
        pdfValues << [meshPoint, lastY]
        for (int i in 1..<plotMesh.size()) {
            // we start to plot before the min value of the obervations because a continuous pdf starts at value =0
            lastMeshPoint = meshPoint
            meshPoint = plotMesh.get(i)
            double y = Estimator._adaptiveKernelBandwidthPdf(meshPoint, priorBandwidth, observations, dataExportMode, plotMesh, meshCounts, bandwidths)
            norm += y * (meshPoint - lastMeshPoint)
            pdfValues << [meshPoint, y]
        }

        if (Math.abs(norm - 1d) > 1e-6) {
            double factor = 1d / norm
            pdfValues.each {List XYPair ->
                XYPair[1] = XYPair[1] * factor
            }
        }
        return pdfValues
    }

    public static double calcBandwidthForGaussKernelEstimate(double stdDev, double IQR, double n) {
        println "use JEstimator instead of Estimator"
        if (IQR > 0) {
            return Estimator.CONST * Math.min(stdDev, IQR) * Math.pow(n, (-1 / 5));
        } else {
            return Estimator.CONST * stdDev * Math.pow(n, (-1 / 5));
        }
    }

    protected static double _adaptiveKernelBandwidthPdf(double x, double priorBandwidth, List observations, boolean dataExportMode, List<Double> meshMidPoints, List<Integer> meshCounts, List<Double> bandwidths) {
        println "use JEstimator instead of Estimator"
        double freq = 0
        double norm = 0

        int n = meshMidPoints.size()


        for (int i = 0; i < n; i++) {
            double bw = bandwidths.get(i)
            if (bw > 0 && Math.abs(x - meshMidPoints[i]) < threshold * bw) {
                freq += meshCounts.get(i) * Math.exp(-(x - meshMidPoints[i]) * (x - meshMidPoints[i]) / (2 * bw * bw)) / bw
            }
            norm += meshCounts.get(i)
        }
        return CONST2 * freq / norm
    }

    protected static double _adaptiveKernelBandwidthCdf(double x, double priorBandwidth, List observations, boolean dataExportMode, List<Double> meshMidPoints, List<Integer> meshCounts, List<Double> bandwidths) {
        println "use JEstimator instead of Estimator"
        double freq = 0
        double norm = 0

        int n = meshMidPoints.size()

        for (int i = 0; i < n; i++) {
            double bw = bandwidths.get(i)
            if (x - meshMidPoints[i] > threshold * bw) {
                freq += meshCounts.get(i);
            } else if (bw > 0 && Math.abs(meshMidPoints[i] - x) < threshold * bw) {
                freq += meshCounts.get(i) * NormalDist.cdf01((x - meshMidPoints[i]) / bw)
            }
            norm += meshCounts.get(i)
        }
        return freq / norm
    }

    /*
    * Maybe this should be adjusted so that the observations is transformed to counts of histogram
    * for a suitable binning
    */

    protected static double adaptiveKernelBandwidthPriorPdf(double x, double priorBandwidth, List observations, boolean dataExportMode, List<Double> meshMidPoints, List<Integer> meshCounts) {
        println "use JEstimator instead of Estimator"
        double freq = 0
        double norm = 0
        int n = meshMidPoints.size()
        double thresholdBW = threshold * priorBandwidth

        for (int i = 0; i < n; i++) {
            if (Math.abs(x - meshMidPoints[i]) < thresholdBW) {
                freq += meshCounts.get(i) * Math.exp(-(x - meshMidPoints[i]) * (x - meshMidPoints[i]) / (2 * priorBandwidth * priorBandwidth))
            }
            norm += meshCounts.get(i)
        }
        return CONST2 / priorBandwidth * freq / norm
    }

    protected static double _gaussKernelBandwidthPdf(double x, List<Double> observations, double t) {
        println "use JEstimator instead of Estimator"
        double sum = 0
        double iStart = 0
        int n = observations.size()
        double scalingFactor = calculateScalingFactor(n, t)
        double thresholdT = threshold * t

        int i = iStart
        while ((i < n) && (x - observations[i] > thresholdT)) i++
        iStart = i
        while ((i < n) && (Math.abs(x - observations[i]) < thresholdT)) {
            sum += Math.exp(-(x - observations[i]) * (x - observations[i]) / (2 * t * t))
            i++
        }

        return scalingFactor * sum
    }

    protected static double calculateScalingFactor(double n, double t) {
        println "use JEstimator instead of Estimator"
        1 / (n * Math.sqrt(2 * Math.PI) * t)
    }

    protected static double _gaussKernelBandwidthCdf(double x, List<Double> observations, double t) {
        println "use JEstimator instead of Estimator"
        double iStart = 0
        double sum = 0
        int n = observations.size()
        double thresholdT = threshold * t

        int i = iStart
        while ((i < n) && (x - observations[i] > thresholdT)) i++
        iStart = i
        sum = i
        while ((i < n) && (Math.abs(x - observations[i]) < thresholdT)) {
            sum += NormalDist.cdf01((x - observations[i]) / t)
            i++
        }
        return sum / n
    }

    protected static List<Double> computeBandwidths(List<Double> meshMidPoints, List<Integer> meshCounts, double priorBandwidth, List observations, boolean dataExportMode) {
        println "use JEstimator instead of Estimator"
        List<Double> bws = []
        int n = meshMidPoints.size()
        double norm = 0.0;
        for (int i = 0; i < n; i++) {
            double density = Estimator.adaptiveKernelBandwidthPriorPdf(meshMidPoints.get(i), priorBandwidth, observations, dataExportMode, meshMidPoints, meshCounts)
            norm += density > 0 ? Math.log(density) : 0;
            bws.add(density)
        }
        norm = Math.exp(norm / n)
        for (int i = 0; i < n; i++) {
            double bw = bws.get(i)
            bw = Math.sqrt(norm / bw) * priorBandwidth;
            bws.set(i, bw)
        }
        return bws
    }
}