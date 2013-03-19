package org.pillarone.riskanalytics.application.util

class MeshCalculations {
  public static int SAMPLE_COUNT = 200  //number of sample points for graphics
  protected static double numberOfStdev = 3.0 //size of plotting to the left of min observations and to the right of max observations
  protected static boolean useBinCountsForPrior = true


  static public List calculateMeshCount(List meshMidPoints, List observations) {
    // prepare the xmesh
    // the mesh values are chosen as the mid points of the bins
    // and prepare the list that will contain the counts
    List meshCounts = []
    if (useBinCountsForPrior) {
      meshCounts = MeshCalculations.getMeshCounts(meshMidPoints, observations)
    } else {
      int meshSize = observations.size();
      for (int i in 0..<meshSize) {
        meshCounts << 1
      }
    }
    return meshCounts
  }


  static protected List<Double> getUnifromMeshForAllSeries(int n, double bandwidth, List series) {
    double max0 = series.collect { List<Double> obs -> obs.get(obs.size() - 1)}.max() 
    double min0 = series.collect { List<Double> obs -> obs.get(0)}.min()
    double delta = (max0 - min0 + 2 * bandwidth) / n
    double x = min0 - bandwidth + delta / 2
    List<Double> xvalues = []
    int j = 0;
    while (j < n) {
      x += delta
      xvalues << x
      j++
    }
    return xvalues
  }

  static protected List<Double> getUniformMesh(int n, double bandwidth, List observations) {
    double max0 = observations.get(-1)
    double min0 = observations.get(0)
    double delta = (max0 - min0 + 2 * bandwidth) / n
    double x = min0 - bandwidth + delta / 2
    List<Double> xvalues = []
    int j = 0;
    while (j < n) {
      x += delta
      xvalues << x
      j++
    }
    return xvalues
  }

  static public List<Double> getUniformMeshWithModifiedEdges(double priorBandwidth,boolean dataExportMode, List observations) {
    int n = SAMPLE_COUNT
    double bandwidth = numberOfStdev * priorBandwidth
    if (dataExportMode) {
      //todo edge modification
      return getUnifromMeshForAllSeries(n, bandwidth,observations)
    }
    int obsSize = observations.size()

    final int numOfEdgeSamples = Math.min(observations.size() / 40, 10)
    double min0 = observations.get(numOfEdgeSamples)
    double max0 = observations.get(obsSize - numOfEdgeSamples - 1)

    // TODO: clean up. it could be that min0 is smaller than the previous observation and hence we don't need it
    //       in this case the following caluclation introduces unnecessary sample points
    double delta = (max0 - min0 + 2 * bandwidth) / (n - 2 * numOfEdgeSamples)
    double x = min0 - bandwidth + delta / 2
    List<Double> xvalues = []

    xvalues <<  observations.get(0) - bandwidth
    for (int i = 0; i < numOfEdgeSamples; i++) {
      xvalues <<  observations.get(i)
    }

    int j = 0;
    while (j < n) {
      x += delta
      xvalues <<  x
      j++
    }
    for (int i = obsSize - numOfEdgeSamples; i < obsSize; i++) {
      xvalues <<  observations.get(i)
    }
    xvalues << observations.get(observations.size() - 1) + bandwidth
    xvalues.sort()
    return xvalues
  }

  /**
   * Assume that the mesh values and the data are in increasing order.
   */
  protected static List<Integer> getMeshCounts(List<Double> mesh, List<Double> data) {
    List<Integer> counts = []
    int dataSize = data.size()
    int meshSize = mesh.size()
    int dataIndex = 0
    int meshIndex = 0
    double meshPoint = mesh.get(0)
    while (meshIndex + 1 < meshSize) {
      int count = 0
      double lastMeshPoint = meshPoint
      meshIndex++
      meshPoint = mesh.get(meshIndex)
      double upperBound = 0.5 * (lastMeshPoint + meshPoint)
      while ((dataIndex < dataSize) && (data.get(dataIndex) < upperBound)) {
        count++
        dataIndex++
      }
      counts << count
    }
    counts << dataSize - dataIndex
    return counts
  }
}