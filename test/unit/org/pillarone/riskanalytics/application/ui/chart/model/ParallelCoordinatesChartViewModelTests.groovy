package org.pillarone.riskanalytics.application.ui.chart.model

import org.jfree.data.category.DefaultCategoryDataset
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode

class ParallelCoordinatesChartViewModelTests extends GroovyTestCase {
    public eventFired = false
    ParallelCoordinatesChartViewModel model

    protected void setUp() {
        LocaleResources.setTestMode()

        ResultTableTreeNode node = new ResultTableTreeNode("outTest")
        node.collector = "testCollector"

        model = new ParallelCoordinatesChartViewTestModel("testChart", new SimulationRun(), [node])
        model.addListener([modelChanged: { eventFired = true }] as IModelChangedListener)
    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testGetDataTable() {
        List keyFigure1 = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
        List keyFigure2 = [[10, 20, 30], [40, 50, 60], [70, 80, 90]]
        model.series = [keyFigure1, keyFigure2]
        model.seriesNames = ["series1", "series2"]
        model.currentPeriod = 2
        model.simulationRun.iterations = 3

        Map exportMap = model.getDataTable()

        assertEquals([period: [2, 2, 2], series1: [1.1, 1.2, 1.3], series2: [2.1, 2.2, 2.3]], exportMap)
    }

    void testMoveSeriesUp() {
        model.series = [[1, 2, 3], [4, 5, 6]]
        model.seriesNames = ["series1", "series2"]
        model.maxs = [1001, 1002]
        model.mins = [1, 2]
        SimpleTableTreeNode node1 = new SimpleTableTreeNode("testNode1")
        SimpleTableTreeNode node2 = new SimpleTableTreeNode("testNode2")
        model.nodes = [node1, node2]
        model.queryPaneModel.nodes = [node1, node2]

        eventFired = false
        model.moveSeriesUp 1
        assertTrue "model shoud fire event", eventFired

        assertEquals([[4, 5, 6], [1, 2, 3]], model.series)
        assertEquals(["series2", "series1"], model.seriesNames)
        assertEquals([1002, 1001], model.maxs)
        assertEquals([2, 1], model.mins)
        assertEquals([node2, node1], model.nodes)

        eventFired = false
        model.moveSeriesUp 0
        assertFalse "model should not fire event", eventFired
    }

    void testMoveSeriesDown() {
        SimpleTableTreeNode node1 = new SimpleTableTreeNode("testNode1")
        SimpleTableTreeNode node2 = new SimpleTableTreeNode("testNode2")
        model.series = [[4, 5, 6], [1, 2, 3]]
        model.seriesNames = ["series2", "series1"]
        model.maxs = [1002, 1001]
        model.mins = [2, 1]
        model.nodes = [node2, node1]
        model.queryPaneModel.nodes = [node1, node2]

        eventFired = false
        model.moveSeriesDown 0
        assertTrue "model should fire event", eventFired

        assertEquals([[1, 2, 3], [4, 5, 6]], model.series)
        assertEquals(["series1", "series2"], model.seriesNames)
        assertEquals([1001, 1002], model.maxs)
        assertEquals([1, 2], model.mins)
        assertEquals([node1, node2], model.nodes)

        eventFired = false
        model.moveSeriesDown 1
        assertFalse "model should not fire event", eventFired
    }

    void testSetPeriodVisibility() {
        assertEquals 0, model.currentPeriod
        assertEquals 0, model.queryPaneModel.defaultPeriod

        eventFired = false
        model.setPeriodVisibility 3, true
        assertTrue "model should fire event", eventFired

        assertEquals 3, model.currentPeriod
        assertEquals 3, model.queryPaneModel.defaultPeriod

        eventFired = false
        model.setPeriodVisibility 2, false
        assertFalse "model should not fire event", eventFired

        assertEquals 3, model.currentPeriod
        assertEquals 3, model.queryPaneModel.defaultPeriod
    }

}

class ParallelCoordinatesChartViewTestModel extends ParallelCoordinatesChartViewModel {
    public ParallelCoordinatesChartViewTestModel(String title, SimulationRun simulationRun, List<SimpleTableTreeNode> nodes) {
        super(title, simulationRun, nodes, false)
    }

    public DefaultCategoryDataset getDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.1, 1, "series1")
        dataset.addValue(1.2, 2, "series1")
        dataset.addValue(1.3, 3, "series1")

        dataset.addValue(2.1, 1, "series2")
        dataset.addValue(2.2, 2, "series2")
        dataset.addValue(2.3, 3, "series2")
        return dataset
    }
}