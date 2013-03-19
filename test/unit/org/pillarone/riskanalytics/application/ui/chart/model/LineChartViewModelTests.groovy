package org.pillarone.riskanalytics.application.ui.chart.model

import groovy.mock.interceptor.MockFor
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.application.util.LocaleResources

public class LineChartViewModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testGetDataTable() {
        MockFor resultAccessor = new MockFor(ResultAccessor)

        resultAccessor.demand.getMean(1..2000) {SimulationRun simulationRun, int periodIndex, String path, String c, String f -> 0}


        ResultTableTreeNode node = new ResultTableTreeNode("outTest")
        node.collector = "testCollector"

        resultAccessor.use {
            LineChartViewModel model = new LineChartViewModel("test", new SimulationRun(name: "testRun", periodCount: 1000), [node])
            model.showPeriodLabels = false

            shouldFail { model.dataTable }

            model.simulationRun.periodCount = 4
            model.seriesNames = ["KF1", "KF2"]
            model.showLine[0, 0] = true
            model.showLine[1, 0] = true

            model.means = [[1, 2, 3, 4], [11, 12, 13, 14],]

            Map result = model.dataTable
            assertEquals result["key figure"], ["KF1", "KF2"]
            assertEquals result["P0"], [1.0, 11.0]
            assertEquals result["P1"], [2.0, 12.0]
            assertEquals result["P2"], [3.0, 13.0]
            assertEquals result["P3"], [4.0, 14.0]

        }
    }


}