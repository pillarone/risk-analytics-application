package org.pillarone.riskanalytics.application.ui.chart.model

import groovy.mock.interceptor.MockFor
import org.jfree.chart.JFreeChart
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor

class HistogramChartViewModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testGetChart() {
        MockFor resultAccessor = new MockFor(ResultAccessor)

        resultAccessor.demand.hasDifferentValues(1..1) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> true}
        resultAccessor.demand.getValues(1..1) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> [1, 2, 3, 4, 5] as double[] }
        resultAccessor.demand.getMin(1..1) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> 0}
        resultAccessor.demand.getMax(1..1) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> 0}

        ResultTableTreeNode node = new ResultTableTreeNode("outTest")
        node.collector = "testCollector"

        resultAccessor.use {
            HistogramChartViewModel model = new HistogramChartViewModel("test", new SimulationRun(name: "testRun", periodCount: 1), [node])
            model.showPeriodLabels = false
            JFreeChart chart = model.getChart()
            assertNotNull chart
        }
    }


}