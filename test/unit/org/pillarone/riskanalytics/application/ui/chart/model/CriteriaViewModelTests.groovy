package org.pillarone.riskanalytics.application.ui.chart.model

import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.chart.model.CriteriaComparator
import org.pillarone.riskanalytics.application.ui.chart.model.CriteriaViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode

class CriteriaViewModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testSelectedComperator() {
        ResultIterationDataViewModel rawDataViewModel = new ResultIterationDataViewModel(new SimulationRun(), [new ResultTableTreeNode("testNode")], false, true, false)
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(rawDataViewModel)
        criteriaViewModel.comparatorModel.setSelectedItem CriteriaComparator.LESS_THAN.toString()
        assertEquals CriteriaComparator.LESS_THAN, criteriaViewModel.selectedComparator
    }

    void testSelectedPeriod() {
        ResultIterationDataViewModel rawDataViewModel = new ResultIterationDataViewModel(new SimulationRun(), [new ResultTableTreeNode("testNode")], false, true, false)
        rawDataViewModel.periodLabels = ["periodLabel1", "periodLabel2", "periodLabel3", "periodLabel4"]
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(rawDataViewModel)
        criteriaViewModel.periodModel.selectedItem = "periodLabel2"
        assertEquals 1, criteriaViewModel.selectedPeriod

        criteriaViewModel.periodModel.selectedItem = "periodLabel4"
        assertEquals 3, criteriaViewModel.selectedPeriod

        criteriaViewModel.periodModel.selectedItem = "in all periods"
        assertNull criteriaViewModel.selectedPeriod
    }

    void testKeyFigureModelSize() {
        List nodeList = [new ResultTableTreeNode("testNode"), new ResultTableTreeNode("testNode2"), new ResultTableTreeNode("testNode3")]
        ResultIterationDataViewModel rawDataViewModel = new ResultIterationDataViewModel(new SimulationRun(), nodeList, false, true, false)
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(rawDataViewModel)
        assertEquals nodeList.size(), criteriaViewModel.keyFigureTypeModel.size
    }

    void testPeriodModelSize() {
        SimulationRun run = new SimulationRun()
        run.periodCount = 50
        ResultIterationDataViewModel rawDataViewModel = new ResultIterationDataViewModel(run, [new ResultTableTreeNode("testNode")], false, true, false)
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(rawDataViewModel)
        assertEquals run.periodCount + 1, criteriaViewModel.periodModel.size
    }
}