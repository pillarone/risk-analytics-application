package org.pillarone.riskanalytics.application.ui.chart.model

import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.SimulationRun

class ParallelCoordinatesQueryPaneModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testPMO_361() {
        SimulationRun run = new SimulationRun()
        run.iterations = 1
        run.periodCount = 3

        ResultTableTreeNode node1 = new ResultTableTreeNode("test1")
        node1.collector = "asdf"
        ResultTableTreeNode node2 = new ResultTableTreeNode("test2")
        node1.collector = "asdf"
        ResultTableTreeNode node3 = new ResultTableTreeNode("test3")
        node1.collector = "asdf"

        ParallelCoordinatesQueryPaneModel model = new ParallelCoordinatesQueryPaneModel(run, [node1, node2, node3], false, false)
        assertEquals 0, model.criterias.size()

        run.iterations = 1000
        model = new ParallelCoordinatesQueryPaneModel(run, [node1, node2, node3], false, false)
        assertEquals 1, model.criterias.size()
        assertEquals model.criterias[0][0].value, 95.0

        run.iterations = 100000
        model = new ParallelCoordinatesQueryPaneModel(run, [node1, node2, node3], false, false)
        assertEquals 1, model.criterias.size()
        assertEquals model.criterias[0][0].value, 99.0

        run.iterations = 201
        model = new ParallelCoordinatesQueryPaneModel(run, [node1, node2, node3], false, false)
        assertEquals 1, model.criterias.size()
        assertEquals model.criterias[0][0].value, 70.0
    }

}