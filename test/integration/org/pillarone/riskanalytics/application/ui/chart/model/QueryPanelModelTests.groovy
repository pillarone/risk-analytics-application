package org.pillarone.riskanalytics.application.ui.chart.model

import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.output.*
import models.core.CoreModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultTransferObject
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultDescriptor
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultWriter

class QueryPanelModelTests extends GroovyTestCase {

    PathMapping path
    FieldMapping field
    CollectorMapping aggregatedSingleCollector
    ResultWriter resultWriter

    void setUp() {
        LocaleResources.setTestMode(true)
        FileImportService.importModelsIfNeeded(['Core'])

    }

    void tearDown() {
        LocaleResources.setTestMode(false)
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

    void testPMO1914() {
        final SimulationRun run = prepareDB()

        final ResultTableTreeNode node = new ResultTableTreeNode("name", CoreModel)
        node.collector = aggregatedSingleCollector.collectorName
        node.resultPath = path.pathName + ":" + field.fieldName
        ResultIterationDataViewModel model = new ResultIterationDataViewModel(run, [node], false, true, true, null)
        model.addCriteriaGroup()
        model.criterias[0][0].valueInterpretationModel.selectedEnum = ValueInterpretationType.ABSOLUTE
        model.criterias[0][0].comparatorModel.selectedEnum = CriteriaComparator.GREATER_THAN
        model.query()

        assertTrue(model.results.contains(1))
        assertEquals(2000, model.resultTableModel.tableValues[0][1])
    }

    protected SimulationRun prepareDB() {
        path = new PathMapping(pathName: "PATH1").save()
        CollectorMapping aggregatedCollector = CollectorMapping.findByCollectorName(AggregatedCollectingModeStrategy.IDENTIFIER)
        CollectorMapping singleCollector = CollectorMapping.findByCollectorName(SingleValueCollectingModeStrategy.IDENTIFIER)
        aggregatedSingleCollector = CollectorMapping.findByCollectorName(AggregatedWithSingleAvailableCollectingModeStrategy.IDENTIFIER)
        field = new FieldMapping(fieldName: "field").save()

        SimulationRun run = createSimulationRun()
        writeResult new SingleValueResult(simulationRun: run, path: path, collector: singleCollector, field: field, period: 0, iteration: 1, value: 1000)
        writeResult new SingleValueResult(simulationRun: run, path: path, collector: singleCollector, field: field, period: 0, iteration: 1, value: 1000)

        writeResult new SingleValueResult(simulationRun: run, path: path, collector: aggregatedSingleCollector, field: field, period: 0, iteration: 1, value: 2000)

        return run
    }

    protected SimulationRun createSimulationRun() {
        SimulationRun run = new SimulationRun()
        run.name = "testRun"
        run.parameterization = ParameterizationDAO.findByName("CoreParameters")
        run.resultConfiguration = ResultConfigurationDAO.findByName("CoreResultConfiguration")
        run.model = CoreModel.name
        run.periodCount = 1
        run.iterations = 2
        assertNotNull run.save()
        resultWriter = new ResultWriter(run.id)
        return run
    }

     protected CriteriaViewModel createQPM(QueryPaneModel model, String path, int period, CriteriaComparator comparator, double value) {
        CriteriaViewModel criteriaViewModel = new TestCriteriaViewModel(model)
        criteriaViewModel.selectedPath = path
        criteriaViewModel.setSelectedComparator(comparator)
        criteriaViewModel.valueInterpretationModel.setSelectedEnum(ValueInterpretationType.ABSOLUTE)
        criteriaViewModel.value = value
        criteriaViewModel.selectedPeriod = period
        return criteriaViewModel
    }

    private void writeResult(SingleValueResult result) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(result.iteration);
        dos.writeInt(1);
        dos.writeDouble(result.value);
        dos.writeLong(0);

        resultWriter.writeResult(new ResultTransferObject(new ResultDescriptor(result.field.id, result.path.id, result.collector.id, result.period), null, bos.toByteArray(), 0));
    }

}

class TestCriteriaViewModel extends CriteriaViewModel {
    String selectedPath

    public TestCriteriaViewModel(QueryPaneModel queryModel) {
        super(queryModel, false)
    }


}