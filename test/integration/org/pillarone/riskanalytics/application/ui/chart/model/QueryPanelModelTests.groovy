package org.pillarone.riskanalytics.application.ui.chart.model

import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.output.*

class QueryPanelModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testCreateCriteriaSubQuerry() {
        SimulationRun run = new SimulationRun()
        run.periodCount = 2
        QueryPaneModel model = new QueryPaneModel(run, [], false, false, false)

        assertEquals "sum(s.value) >= 123.12", model.createCriteriaSubQuerry(createQPM(model, "TESTPATH", 1, CriteriaComparator.GREATER_EQUALS, 123.12))
        assertEquals "sum(s.value) = 1.0", model.createCriteriaSubQuerry(createQPM(model, "TESTPATH", 1, CriteriaComparator.EQUALS, 1))
        assertEquals "sum(s.value) <= 123.12", model.createCriteriaSubQuerry(createQPM(model, "TESTPATH", 1, CriteriaComparator.LESS_EQUALS, 123.12))
        assertEquals "sum(s.value) > 123.12", model.createCriteriaSubQuerry(createQPM(model, "TESTPATH", 1, CriteriaComparator.GREATER_THAN, 123.12))
    }

    //todo runs alone but not during cruise
    /*void testClearCriteriaList() {
        SimulationRun run = new SimulationRun()
        run.periodCount = 2
        QueryPaneModel model = new QueryPaneModel(run, [], false, false, false)

        List unfilteredList = [
                [createQPM(model, "TESTPATH", 1, Comparator.EQUALS, 1), createQPM(model, "TESTPATH", 2, Comparator.EQUALS, 1)],
                [createQPM(model, "TESTPATH2", 2, Comparator.EQUALS, 1), createQPM(model, "TESTPATH", 2, Comparator.EQUALS, 1)]
        ]

        List expectedList = [[unfilteredList[0][0]]]
        List expectedList2 = [[unfilteredList[0][1]], [unfilteredList[1][1]]]

        model.criterias = unfilteredList
        assertEquals expectedList, model.clearCriteriaList("TESTPATH", 1)
        assertEquals([], model.clearCriteriaList("TESTPATH2", 1))
        assertEquals(expectedList2, model.clearCriteriaList("TESTPATH", 2))
    }*/


    protected CriteriaViewModel createQPM(QueryPaneModel model, String path, int period, CriteriaComparator comparator, double value) {
        CriteriaViewModel criteriaViewModel = new TestCriteriaViewModel(model)
        criteriaViewModel.selectedPath = path
        criteriaViewModel.setSelectedComparator(comparator)
        criteriaViewModel.valueInterpretationModel.setSelectedEnum(ValueInterpretationType.ABSOLUTE)
        criteriaViewModel.value = value
        criteriaViewModel.selectedPeriod = period
        return criteriaViewModel
    }

    protected SimulationRun prepareDBForQueryResultsHQL() {
        PathMapping path = new PathMapping(pathName: "PATH1").save()
        CollectorMapping collector = new CollectorMapping(collectorName: "collector").save()
        FieldMapping field = new FieldMapping(fieldName: "field").save()
        SimulationRun run = createSR()
        new SingleValueResult(simulationRun: run, path: path, collector: collector, field: field, period: 1, iteration: 1, value: 1000).save()
        new SingleValueResult(simulationRun: run, path: path, collector: collector, field: field, period: 1, iteration: 1, value: 1000).save()

        new SingleValueResult(simulationRun: run, path: path, collector: collector, field: field, period: 1, iteration: 2, value: 500).save()
        new SingleValueResult(simulationRun: run, path: path, collector: collector, field: field, period: 1, iteration: 3, value: 1).save()

        return run
    }

    protected SimulationRun createSR() {
        FileImportService.importModelsIfNeeded(['CapitalEagle'])
        SimulationRun run = new SimulationRun(periodCount: 2, name: "testRun")
        run.name = "SR"
        run.parameterization = ParameterizationDAO.list()[0]
        run.resultConfiguration = ResultConfigurationDAO.list()[0]
        run.model = "MODEL"
        run.periodCount = 2
        run.iterations = 10
        run.modelVersionNumber = "VERSION_NUMBER"
        run.save()
        return run
    }

}

class TestCriteriaViewModel extends CriteriaViewModel {
    String selectedPath

    public TestCriteriaViewModel(QueryPaneModel queryModel) {
        super(queryModel, false)
    }


}