package org.pillarone.riskanalytics.application.ui.result.model

import models.core.CoreModel
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.chart.model.CriteriaComparator
import org.pillarone.riskanalytics.application.ui.chart.model.CriteriaViewModel
import org.pillarone.riskanalytics.application.ui.chart.model.ValueIntepretationType
import org.pillarone.riskanalytics.application.util.LocaleResources

class ResultIterationDataViewModelTests extends GroovyTestCase {

    SimulationRun simulationRun
    PathMapping path1
    PathMapping path2
    FieldMapping field
    FieldMapping field2
    CollectorMapping collector

    void setUp() {
        LocaleResources.setTestMode()

        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreParameters'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['CoreResultConfiguration'])
        simulationRun = new SimulationRun()
        simulationRun.name = "testRun"
        simulationRun.parameterization = ParameterizationDAO.findByName('CoreParameters')
        simulationRun.resultConfiguration = ResultConfigurationDAO.findByName('CoreResultConfiguration')
        simulationRun.model = CoreModel.name
        simulationRun.periodCount = 2
        simulationRun.iterations = 5
        simulationRun.randomSeed = 0
        simulationRun.modelVersionNumber = "1"

        simulationRun = simulationRun.save(flush: true)

        path1 = PathMapping.findByPathName('testPath1')
        if (path1 == null) {
            path1 = new PathMapping(pathName: 'testPath1').save()
        }

        path2 = PathMapping.findByPathName('testPath2')
        if (path2 == null) {
            path2 = new PathMapping(pathName: 'testPath2').save()
        }

        field = FieldMapping.findByFieldName('Ultimate')
        if (field == null) {
            field = new FieldMapping(fieldName: 'ultimate').save()
        }

        field2 = FieldMapping.findByFieldName('value')
        if (field2 == null) {
            field2 = new FieldMapping(fieldName: 'value').save()
        }

        collector = CollectorMapping.findByCollectorName('collector')
        if (collector == null) {
            collector = new CollectorMapping(collectorName: 'collector').save()
        }
    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
        super.tearDown();
    }

    void testCriteriaList() {
        ResultIterationDataViewModel rawDataViewModel = new ResultIterationDataViewModel(new SimulationRun(), [new ResultTableTreeNode("testNode")], false, true, false)
        assertEquals 1, rawDataViewModel.getCriteriaGroupCount()
        assertEquals 1, rawDataViewModel.getCriteriaGroup(0).size()

        rawDataViewModel.addCriteria 0
        assertEquals 2, rawDataViewModel.getCriteriaGroup(0).size()

        rawDataViewModel.addCriteriaGroup()
        assertEquals 2, rawDataViewModel.getCriteriaGroupCount()
    }

    void testPathOrder() {
        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, [new ResultTableTreeNode("testPath1"), new ResultTableTreeNode("testPath2")], false)
        assertEquals 2, model.paths.size()
        assertEquals "testPath1", model.paths.get(0)
        assertEquals "testPath2", model.paths.get(1)

        assertEquals 2, model.shortPaths.size()
        assertEquals "test path1", model.shortPaths.get(0)
        assertEquals "test path2", model.shortPaths.get(1)

        model = new ResultIterationDataViewModel(simulationRun, [new ResultTableTreeNode("testPath2"), new ResultTableTreeNode("testPath1")], false)
        assertEquals 2, model.paths.size()
        assertEquals "testPath1", model.paths.get(0)
        assertEquals "testPath2", model.paths.get(1)

        assertEquals 2, model.shortPaths.size()
        assertEquals "test path1", model.shortPaths.get(0)
        assertEquals "test path2", model.shortPaths.get(1)
    }

    void testSimpleQuery() {
        assertNotNull simulationRun
        initResults()

        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, createResultNodes(), false)
        model.criterias.clear()
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.GREATER_EQUALS
        criteriaViewModel.value = 4
        criteriaViewModel.valueIntepretationModel.selectedEnum = ValueIntepretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "test path1 / ultimate"

        model.criterias.add([criteriaViewModel])

        model.query()

        assertEquals 2, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "test path1 / ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "test path2 / ultimate P0", model.resultTableModel.getColumnName(2)
        assertEquals "test path1 / ultimate P1", model.resultTableModel.getColumnName(3)
        assertEquals "test path2 / ultimate P1", model.resultTableModel.getColumnName(4)

        assertEquals 3, model.resultTableModel.getValueAt(0, 0)
        assertEquals 4, model.resultTableModel.getValueAt(0, 1)
        assertEquals 12, model.resultTableModel.getValueAt(0, 2)
        assertEquals 8, model.resultTableModel.getValueAt(0, 3)
        assertEquals 24, model.resultTableModel.getValueAt(0, 4)

        assertEquals 4, model.resultTableModel.getValueAt(1, 0)
        assertEquals 5, model.resultTableModel.getValueAt(1, 1)
        assertEquals 15, model.resultTableModel.getValueAt(1, 2)
        assertEquals 10, model.resultTableModel.getValueAt(1, 3)
        assertEquals 30, model.resultTableModel.getValueAt(1, 4)
    }

    void testSimpleQueryWithDifferentFields() {
        assertNotNull simulationRun
        initResultsWithDifferentFields()

        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, createResultNodes(), false)
        model.criterias.clear()
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.GREATER_EQUALS
        criteriaViewModel.value = 4
        criteriaViewModel.valueIntepretationModel.selectedEnum = ValueIntepretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "test path1 / ultimate"

        model.criterias.add([criteriaViewModel])

        model.query()

        assertEquals 2, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "test path1 / ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "test path2 / ultimate P0", model.resultTableModel.getColumnName(2)
        assertEquals "test path1 / ultimate P1", model.resultTableModel.getColumnName(3)
        assertEquals "test path2 / ultimate P1", model.resultTableModel.getColumnName(4)

        assertEquals 3, model.resultTableModel.getValueAt(0, 0)
        assertEquals 4, model.resultTableModel.getValueAt(0, 1)
        assertEquals 12, model.resultTableModel.getValueAt(0, 2)
        assertEquals 8, model.resultTableModel.getValueAt(0, 3)
        assertEquals 24, model.resultTableModel.getValueAt(0, 4)

        assertEquals 4, model.resultTableModel.getValueAt(1, 0)
        assertEquals 5, model.resultTableModel.getValueAt(1, 1)
        assertEquals 15, model.resultTableModel.getValueAt(1, 2)
        assertEquals 10, model.resultTableModel.getValueAt(1, 3)
        assertEquals 30, model.resultTableModel.getValueAt(1, 4)
    }

    void testOrQuery() {
        assertNotNull simulationRun
        initResults()

        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, createResultNodes(), false)
        model.criterias.clear()
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.GREATER_EQUALS
        criteriaViewModel.value = 4
        criteriaViewModel.valueIntepretationModel.selectedEnum = ValueIntepretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "test path1 / ultimate"

        model.criterias.add([criteriaViewModel])

        criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.LESS_EQUALS
        criteriaViewModel.value = 5
        criteriaViewModel.valueIntepretationModel.selectedEnum = ValueIntepretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "test path1 / ultimate"

        model.criterias.get(0).add(criteriaViewModel)

        model.query()

        assertEquals 2, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "test path1 / ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "test path2 / ultimate P0", model.resultTableModel.getColumnName(2)
        assertEquals "test path1 / ultimate P1", model.resultTableModel.getColumnName(3)
        assertEquals "test path2 / ultimate P1", model.resultTableModel.getColumnName(4)

        assertEquals 3, model.resultTableModel.getValueAt(0, 0)
        assertEquals 4, model.resultTableModel.getValueAt(0, 1)
        assertEquals 12, model.resultTableModel.getValueAt(0, 2)
        assertEquals 8, model.resultTableModel.getValueAt(0, 3)
        assertEquals 24, model.resultTableModel.getValueAt(0, 4)

        assertEquals 4, model.resultTableModel.getValueAt(1, 0)
        assertEquals 5, model.resultTableModel.getValueAt(1, 1)
        assertEquals 15, model.resultTableModel.getValueAt(1, 2)
        assertEquals 10, model.resultTableModel.getValueAt(1, 3)
        assertEquals 30, model.resultTableModel.getValueAt(1, 4)
    }

    void testAndQuery() {
        assertNotNull simulationRun
        initResults()

        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, createResultNodes(), false)
        model.criterias.clear()

        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.GREATER_EQUALS
        criteriaViewModel.value = 5
        criteriaViewModel.valueIntepretationModel.selectedEnum = ValueIntepretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "test path1 / ultimate"

        model.criterias.add([criteriaViewModel])

        criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.LESS_EQUALS
        criteriaViewModel.value = 1
        criteriaViewModel.valueIntepretationModel.selectedEnum = ValueIntepretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "test path1 / ultimate"

        model.criterias.add([criteriaViewModel])

        model.query()

        assertEquals 2, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "test path1 / ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "test path2 / ultimate P0", model.resultTableModel.getColumnName(2)
        assertEquals "test path1 / ultimate P1", model.resultTableModel.getColumnName(3)
        assertEquals "test path2 / ultimate P1", model.resultTableModel.getColumnName(4)

        assertEquals 0, model.resultTableModel.getValueAt(0, 0)
        assertEquals 1, model.resultTableModel.getValueAt(0, 1)
        assertEquals 3, model.resultTableModel.getValueAt(0, 2)
        assertEquals 2, model.resultTableModel.getValueAt(0, 3)
        assertEquals 6, model.resultTableModel.getValueAt(0, 4)

        assertEquals 4, model.resultTableModel.getValueAt(1, 0)
        assertEquals 5, model.resultTableModel.getValueAt(1, 1)
        assertEquals 15, model.resultTableModel.getValueAt(1, 2)
        assertEquals 10, model.resultTableModel.getValueAt(1, 3)
        assertEquals 30, model.resultTableModel.getValueAt(1, 4)
    }

    void testEmptyQuery() {
        assertNotNull simulationRun
        initResults()

        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, createResultNodes(), false)
        model.criterias.clear()

        model.query()

        assertEquals simulationRun.iterations, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "test path1 / Ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "test path2 / Ultimate P0", model.resultTableModel.getColumnName(2)
        assertEquals "test path1 / Ultimate P1", model.resultTableModel.getColumnName(3)
        assertEquals "test path2 / Ultimate P1", model.resultTableModel.getColumnName(4)

        assertEquals 0, model.resultTableModel.getValueAt(0, 0)
        assertEquals 1, model.resultTableModel.getValueAt(0, 1)
        assertEquals 3, model.resultTableModel.getValueAt(0, 2)
        assertEquals 2, model.resultTableModel.getValueAt(0, 3)
        assertEquals 6, model.resultTableModel.getValueAt(0, 4)

        assertEquals 4, model.resultTableModel.getValueAt(4, 0)
        assertEquals 5, model.resultTableModel.getValueAt(4, 1)
        assertEquals 15, model.resultTableModel.getValueAt(4, 2)
        assertEquals 10, model.resultTableModel.getValueAt(4, 3)
        assertEquals 30, model.resultTableModel.getValueAt(4, 4)

        model.orderByPath = true
        model.query()

        assertEquals simulationRun.iterations, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "test path1 / Ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "test path1 / Ultimate P1", model.resultTableModel.getColumnName(2)
        assertEquals "test path2 / Ultimate P0", model.resultTableModel.getColumnName(3)
        assertEquals "test path2 / Ultimate P1", model.resultTableModel.getColumnName(4)

        assertEquals 0, model.resultTableModel.getValueAt(0, 0)
        assertEquals 1, model.resultTableModel.getValueAt(0, 1)
        assertEquals 2, model.resultTableModel.getValueAt(0, 2)
        assertEquals 3, model.resultTableModel.getValueAt(0, 3)
        assertEquals 6, model.resultTableModel.getValueAt(0, 4)

        assertEquals 4, model.resultTableModel.getValueAt(4, 0)
        assertEquals 5, model.resultTableModel.getValueAt(4, 1)
        assertEquals 10, model.resultTableModel.getValueAt(4, 2)
        assertEquals 15, model.resultTableModel.getValueAt(4, 3)
        assertEquals 30, model.resultTableModel.getValueAt(4, 4)

    }

    private void initResults() {
        simulationRun.iterations.times {int iteration ->
            simulationRun.periodCount.times {int period ->
                assertNotNull new SingleValueResult(simulationRun: simulationRun, period: period, iteration: iteration, valueIndex: 0, value: (iteration + 1) * (period + 1), field: field, path: path1, collector: collector).save()
                assertNotNull new SingleValueResult(simulationRun: simulationRun, period: period, iteration: iteration, valueIndex: 0, value: (iteration + 1) * (period + 1) * 3, field: field, path: path2, collector: collector).save()
            }
        }
    }

    private void initResultsWithDifferentFields() {
        simulationRun.iterations.times {int iteration ->
            simulationRun.periodCount.times {int period ->
                assertNotNull new SingleValueResult(simulationRun: simulationRun, period: period, iteration: iteration, valueIndex: 0, value: (iteration + 1) * (period + 1), field: field, path: path1, collector: collector).save()
                assertNotNull new SingleValueResult(simulationRun: simulationRun, period: period, iteration: iteration, valueIndex: 0, value: (iteration + 1) * (period + 1) * 3, field: field, path: path2, collector: collector).save()
                assertNotNull new SingleValueResult(simulationRun: simulationRun, period: period, iteration: iteration, valueIndex: 0, value: 5, field: field2, path: path1, collector: collector).save()
            }
        }
    }

    private List createResultNodes() {
        def res = []
        2.times {int i ->
            def parent = new SimpleTableTreeNode("testPath${i + 1}")
            def result = new ResultTableTreeNode("ultimate")
            parent.add(result)
            res << result
        }
        return res
    }


}