package org.pillarone.riskanalytics.application.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class JMeshCalculations {
    public static int SAMPLE_COUNT = 200;  //number of sample points for graphics
    protected static double numberOfStdev = 3.0; //size of plotting to the left of min observations and to the right of max observations
    protected static boolean useBinCountsForPrior = true;

    static public List<Double> getUniformMeshWithModifiedEdges(double priorBandwidth, boolean dataExportMode, List<Double> observations) {
        int n = SAMPLE_COUNT;
        double bandwidth = numberOfStdev * priorBandwidth;
        if (dataExportMode) {
            //todo edge modification
            List<List<Double>> list = new ArrayList<List<Double>>();
            list.add(observations);
            return getUnifromMeshForAllSeries(n, bandwidth, list);
        }
        int obsSize = observations.size();
        final int numOfEdgeSamples = Math.min(observations.size() / 40, 10);
        double min0 = observations.get(numOfEdgeSamples);
        double max0 = observations.get(obsSize - numOfEdgeSamples - 1);

        // TODO: clean up. it could be that min0 is smaller than the previous observation and hence we don't need it
        //       in this case the following caluclation introduces unnecessary sample points
        double delta = (max0 - min0 + 2 * bandwidth) / (n - 2 * numOfEdgeSamples);
        double x = min0 - bandwidth + delta / 2;

        List<Double> xvalues = new ArrayList<Double>();

        xvalues.add(observations.get(0) - bandwidth);
        for (int i = 0; i < numOfEdgeSamples; i++) {
            xvalues.add(observations.get(i));
        }

        int j = 0;
        while (j < n) {
            x += delta;
            xvalues.add(x);
            j++;
        }

        for (int i = obsSize - numOfEdgeSamples; i < obsSize; i++) {
            xvalues.add(observations.get(i));
        }
        xvalues.add(observations.get(observations.size() - 1) + bandwidth);
        Collections.sort(xvalues);
        return xvalues;
    }

    static protected List<Double> getUnifromMeshForAllSeries(int n, double bandwidth, List<List<Double>> series) {
        double max0 = 0;
        double min0 = Double.MAX_VALUE;
        for (List<Double> list : series) {
            max0 = Math.max(max0, list.get(list.size() - 1));
            min0 = Math.min(min0, list.get(0));
        }

        double delta = (max0 - min0 + 2 * bandwidth) / n;
        double x = min0 - bandwidth + delta / 2;
        List<Double> xvalues = new ArrayList<Double>();
        int j = 0;
        while (j < n) {
            x += delta;
            xvalues.add(x);
            j++;
        }
        return xvalues;
    }


    static public List<Integer> calculateMeshCount(List<Double> meshMidPoints, List<Double> observations) {
        // prepare the xmesh
        // the mesh values are chosen as the mid points of the bins
        // and prepare the list that will contain the counts
        List meshCounts = new ArrayList<Integer>();
        if (useBinCountsForPrior) {
            meshCounts = JMeshCalculations.getMeshCounts(meshMidPoints, observations);
        } else {
            int meshSize = observations.size();
            for (int i = 0; i < meshSize; i++) {
                meshCounts.add(1);
            }
        }
        return meshCounts;
    }

    /**
     * Assume that the mesh values and the data are in increasing order.
     */
    protected static List<Integer> getMeshCounts(List<Double> mesh, List<Double> data) {
        List<Integer> counts = new ArrayList<Integer>();
        int dataSize = data.size();
        int meshSize = mesh.size();
        int dataIndex = 0;
        int meshIndex = 0;
        double meshPoint = mesh.get(0);
        while (meshIndex + 1 < meshSize) {
            int count = 0;
            double lastMeshPoint = meshPoint;
            meshIndex++;
            meshPoint = mesh.get(meshIndex);
            double upperBound = 0.5 * (lastMeshPoint + meshPoint);
            while ((dataIndex < dataSize) && (data.get(dataIndex) < upperBound)) {
                count++;
                dataIndex++;
            }
            counts.add(count);
        }
        counts.add(dataSize - dataIndex);
        return counts;
    }
}
