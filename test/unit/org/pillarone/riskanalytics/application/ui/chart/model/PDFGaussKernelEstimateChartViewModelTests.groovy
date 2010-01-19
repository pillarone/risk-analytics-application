package org.pillarone.riskanalytics.application.ui.chart.model

import groovy.mock.interceptor.MockFor
import groovy.mock.interceptor.StubFor
import org.jfree.chart.JFreeChart
import org.jfree.data.xy.XYSeries
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.dataaccess.function.Percentile
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.util.MeshCalculations
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor

class PDFGaussKernelEstimateChartViewModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testGetChart() {
        MockFor resultAccessor = new MockFor(ResultAccessor)
        resultAccessor.demand.hasDifferentValues(1..1) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> true}
        resultAccessor.demand.getValues(1..1) {simulationRun, periodIndex, path, String s, String s2 -> [1d, 2d, 3d, 4d, 5d]}
        resultAccessor.demand.getMin(1..1) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> 1}
        resultAccessor.demand.getMax(1..1) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> 5}
        resultAccessor.demand.getMean(1..1) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> 3}
        resultAccessor.demand.getStdDev(1..1) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> 2}
        resultAccessor.demand.getPercentile(2..2) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2, percentile -> 2}

        ResultTableTreeNode node = new ResultTableTreeNode("outTest")
        node.collector = "testCollector"

        resultAccessor.use {
            PDFGaussKernelEstimateChartViewModel model = new PDFGaussKernelEstimateChartViewTestModel("test", new SimulationRun(name: "testRun", periodCount: 1), [node])
            model.showPeriodLabels = false
            JFreeChart chart = model.getChart()
            assertNotNull chart
        }
    }

    void testCreateSeries() {
        MockFor resultAccessor = new MockFor(ResultAccessor)
        resultAccessor.demand.hasDifferentValues(1..1) {SimulationRun simulationRun, int periodIndex, String path, String co, String f -> true}
        resultAccessor.demand.getValues(1..1) {SimulationRun simulationRun, period, String path, String co, String f -> [1d, 2d, 3d, 4d, 5d]}
        resultAccessor.demand.getMin(1..1) {SimulationRun simulationRun, int periodIndex, String path, String co, String f -> 1}
        resultAccessor.demand.getMax(1..1) {SimulationRun simulationRun, int periodIndex, String path, String co, String f -> 5}
        resultAccessor.demand.getMean(1..1) {SimulationRun simulationRun, int periodIndex, String path, String co, String f -> 3}
        resultAccessor.demand.getStdDev(1..1) {SimulationRun simulationRun, int periodIndex, String path, String co, String f -> 2}

        StubFor percentile = new StubFor(Percentile)
        percentile.demand.evaluate(0..4) {def a, def b, def c -> Math.random()}
        percentile.demand.getI18nName(0..4) {-> ""}

        ResultTableTreeNode node = new ResultTableTreeNode("outTest")
        node.collector = "testCollector"

        resultAccessor.use {
            percentile.use {
                PDFGaussKernelEstimateChartViewModel model = new PDFGaussKernelEstimateChartViewModel("TEST", new SimulationRun(name: "testRun", periodCount: 1), [node])
                model.observations = [1d, 2d, 3d, 4d]
                XYSeries series = model.createSeries("test")
                assertEquals "test", series.key
                assertEquals MeshCalculations.SAMPLE_COUNT + 2, series.getItems().size()
            }
        }

    }


}

class PDFGaussKernelEstimateChartViewTestModel extends PDFGaussKernelEstimateChartViewModel {
    public PDFGaussKernelEstimateChartViewTestModel(String title, SimulationRun simulationRun, List<SimpleTableTreeNode> nodes) {
        super(title, simulationRun, nodes)
    }

    protected void addHistogram(JFreeChart chart, String legendTitle, double lowerBound, double upperBound) {}
}