package org.pillarone.riskanalytics.application.ui.chart.model

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.core.output.SimulationRun

@CompileStatic
public class ParallelCoordinatesQueryPaneModel extends QueryPaneModel {
    public static int N_MAX = 200
    public static double ROUND_PRECISION = 10

    public ParallelCoordinatesQueryPaneModel(SimulationRun simulationRun, List<SimpleTableTreeNode> nodes, boolean autoQueryOnCreate = true, boolean showPeriodLabels = true) {
        super(simulationRun, nodes, false, false, showPeriodLabels)
        criterias = [[]]
        int iterationCount = simulationRun.iterations
        if (N_MAX < iterationCount) {
            int k = nodes.size()
            double p = 1.0 - N_MAX / (iterationCount * k)
            double pRounded
            if (p > 0.95) {
                pRounded = 0.99
            } else if (p > 0.9) {
                pRounded = 0.95
            } else {
                pRounded = Math.round((p + 0.049999999999) * ROUND_PRECISION) / ROUND_PRECISION
            }
            if (nodes && nodes.size() > 0) {
                CriteriaViewModel model = new CriteriaViewModel(this, false)
                model.selectedPath = nodes[0].getShortDisplayPath(nodes)
                model.selectedComparator = CriteriaComparator.GREATER_THAN
                model.value = pRounded * 100
                criterias << [model]
            }
        }
        orderByPath = false
        if (autoQueryOnCreate) {
            query()
        }
    }
}