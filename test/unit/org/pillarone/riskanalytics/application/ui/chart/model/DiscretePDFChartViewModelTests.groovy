package org.pillarone.riskanalytics.application.ui.chart.model

import groovy.mock.interceptor.MockFor
import org.jfree.chart.JFreeChart
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor

class DiscretePDFChartViewModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode(true)
    }

    void tearDown() {
        LocaleResources.setTestMode(false)
    }

    void testGetChart() {
        MockFor resultAccessor = new MockFor(ResultAccessor)
        resultAccessor.demand.hasDifferentValues(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> true}
        resultAccessor.demand.getValues(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> [1, 2, 3, 4, 5]}
        resultAccessor.demand.getMin(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> 1}
        resultAccessor.demand.getMax(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> 5}
        resultAccessor.demand.getMean(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> 3}
        resultAccessor.demand.getStdDev(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> 2}

        resultAccessor.use {
            DiscretePDFChartViewModel model = new DiscretePDFChartViewModel("test", new SimulationRun(name: "testRun"), [new ResultTableTreeNode("outTest")])
            JFreeChart chart = model.getChart()
            assertNotNull chart
        }
    }

    void testMaxBinSize() {
        MockFor resultAccessor = new MockFor(ResultAccessor)
        resultAccessor.demand.hasDifferentValues(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> true}
        resultAccessor.demand.getValues(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> [1, 2, 3, 4, 5]}
        resultAccessor.demand.getMin(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> 100}
        resultAccessor.demand.getMax(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> 500}
        resultAccessor.demand.getMean(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> 3}
        resultAccessor.demand.getStdDev(1..1) {SimulationRun simulationRun, int period, String path, String collector, String field -> 2}

        resultAccessor.use {
            DiscretePDFChartViewModel model = new DiscretePDFChartViewModel("test", new SimulationRun(name: "testRun"), [new ResultTableTreeNode("outTest")])

            assertEquals(model.maxBinSize, 500 - 100)
        }
    }

}