package org.pillarone.riskanalytics.application.ui.chart.model

import groovy.mock.interceptor.StubFor
import org.jfree.chart.JFreeChart
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor

class ScatterChartViewModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testGetChart() {
        StubFor resultAccessor = new StubFor(ResultAccessor)

        resultAccessor.demand.hasDifferentValues(4..4) {SimulationRun simulationRun, int periodIndex, String path, String c, String s -> true}
        resultAccessor.demand.getValues(4..4) {SimulationRun simulationRun, int periodIndex, String path, String c, String s -> [1, 2, 3, 4, 5]}
        resultAccessor.demand.getMean(4..4) {SimulationRun simulationRun, int periodIndex, String path, String c, String s -> 3}
        resultAccessor.demand.getStdDev(4..4) {SimulationRun simulationRun, int periodIndex, String path, String c, String s -> 2}

        ResultTableTreeNode node = new ResultTableTreeNode("outTest")
        node.collector = "testCollector"
        ResultTableTreeNode node2 = new ResultTableTreeNode("outTest")
        node.collector = "testCollector"

        resultAccessor.use {
            SimulationRun run = new SimulationRun(name: "testRun")
            run.periodCount = 1
            ScatterChartViewModel model = new ScatterChartViewModel("test", run, [node2, node])
            model.showPeriodLabels = false
            JFreeChart chart = model.getChart()
            assertNotNull chart
        }
    }

    void testGetDataTable() {
        ScatterChartViewModel model = new TestScatterChartViewModel("testTitle", new SimulationRun(name: "testRun", periodCount: 1), [1, 2])
        model.showPeriodLabels = false
        Map dataTable = model.dataTable
        assertEquals 3, dataTable.size()
        assertEquals(["P0", "P0", "P0", "P0"], dataTable["period"])
        assertEquals([1, 2, 3, 4], dataTable["testSeries1"])
        assertEquals([5, 6, 7, 8], dataTable["testSeries2"])
    }
}

class TestScatterChartViewModel extends ScatterChartViewModel {
    public TestScatterChartViewModel(String title, SimulationRun simulationRun, List nodes) {
        super(title, simulationRun, nodes)
    }

    protected void loadData() {
        onlyStochasticSeries = true
        series << [[1, 2, 3, 4]]
        series << [[5, 6, 7, 8]]

        seriesNames << "testSeries1"
        seriesNames << "testSeries2"
    }
}