package org.pillarone.riskanalytics.application.ui.chart.model

import groovy.mock.interceptor.MockFor
import org.jfree.chart.JFreeChart
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.core.output.SimulationRun

class CDFGaussKernelEstimateChartViewModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testGetChart() {
        MockFor resultAccessor = new MockFor(ResultAccessor)

        resultAccessor.demand.hasDifferentValues(1..1) {SimulationRun simulationRun, int periodIndex, String path, String collector, String field -> true}
        resultAccessor.demand.getPercentile(2..2) {SimulationRun simulationRun, int periodIndex, String path, String collector, String field, percentile, QuantilePerspective perspective -> 2}
        resultAccessor.demand.getValues(1..1) {SimulationRun simulationRun, int periodIndex, String path, String collector, String field -> [1d, 2d, 3d, 4d, 5d] as double[] }
        resultAccessor.demand.getMin(1..1) {SimulationRun simulationRun, int periodIndex, String path, String collector, String field -> 1}
        resultAccessor.demand.getMax(1..1) {SimulationRun simulationRun, int periodIndex, String path, String collector, String field -> 5}
        resultAccessor.demand.getMean(1..1) {SimulationRun simulationRun, int periodIndex, String path, String collector, String field -> 3}
        resultAccessor.demand.getStdDev(1..1) {SimulationRun simulationRun, int periodIndex, String path, String collector, String field -> 2}
        resultAccessor.demand.getPercentile(3..3) {SimulationRun simulationRun, int periodIndex, String path, String collector, String field, percentile, QuantilePerspective perspective -> 2}

        ResultTableTreeNode node = new ResultTableTreeNode("outTest")
        node.collector = "testCollector"

        resultAccessor.use {
            CDFGaussKernelEstimateChartViewModel model = new CDFGaussKernelEstimateChartViewModel("test", new SimulationRun(name: "testRun", periodCount: 1), [node])
            model.showPeriodLabels = false
            JFreeChart chart = model.getChart()
            assertNotNull chart
        }
    }

}