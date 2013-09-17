package org.pillarone.riskanalytics.application.ui.result.model

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.chart.model.CriteriaComparator
import org.pillarone.riskanalytics.application.ui.chart.model.CriteriaViewModel
import org.pillarone.riskanalytics.application.ui.chart.model.ValueInterpretationType
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.output.*
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultTransferObject
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultDescriptor
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultWriter
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.components.ComponentUtils

class ResultIterationDataViewModelTests extends GroovyTestCase {

    SimulationRun simulationRun
    PathMapping path1
    PathMapping path2
    FieldMapping field
    FieldMapping field2
    CollectorMapping collector

    private ResultWriter resultWriter

    void setUp() {
        ResultAccessor.clearCaches()
        LocaleResources.setTestMode()

        new ParameterizationImportService().compareFilesAndWriteToDB(['Application'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['Application'])
        simulationRun = new SimulationRun()
        simulationRun.name = "testRun"
        simulationRun.parameterization = ParameterizationDAO.findByName('ApplicationParameters')
        simulationRun.resultConfiguration = ResultConfigurationDAO.findByName('ApplicationResultConfiguration')
        simulationRun.model = ApplicationModel.name
        simulationRun.periodCount = 2
        simulationRun.iterations = 5
        simulationRun.randomSeed = 0

        simulationRun = simulationRun.save(flush: true)

        resultWriter = new ResultWriter(simulationRun.id)

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

        collector = CollectorMapping.findByCollectorName(AggregatedCollectingModeStrategy.IDENTIFIER)
        if (collector == null) {
            collector = new CollectorMapping(collectorName: AggregatedCollectingModeStrategy.IDENTIFIER).save()
        }
    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
        super.tearDown();
    }

    void testCriteriaList() {
        ResultIterationDataViewModel rawDataViewModel = new ResultIterationDataViewModel(new SimulationRun(), [new ResultTableTreeNode("testNode")], false, true, false, null)
        rawDataViewModel.addCriteriaGroup()
        assertEquals 1, rawDataViewModel.getCriteriaGroupCount()
        assertEquals 1, rawDataViewModel.getCriteriaGroup(0).size()

        rawDataViewModel.addCriteria 0
        assertEquals 2, rawDataViewModel.getCriteriaGroup(0).size()

        rawDataViewModel.addCriteriaGroup()
        assertEquals 2, rawDataViewModel.getCriteriaGroupCount()
    }

    void testPathOrder() {
        ResultTableTreeNode node1 = new ResultTableTreeNode("testPath1")
        node1.resultPath = "testPath1:ultimate"
        ResultTableTreeNode node2 = new ResultTableTreeNode("testPath2")
        node2.resultPath = "testPath2:ultimate"
        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, [node1, node2], false, true, true, null)
        assertEquals 2, model.paths.size()
        assertEquals "testPath1", model.paths.get(0)
        assertEquals "testPath2", model.paths.get(1)

        assertEquals 2, model.shortPaths.size()
        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)}", model.shortPaths.get(0)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)}", model.shortPaths.get(1)

        model = new ResultIterationDataViewModel(simulationRun, [node2, node1], false, true, true, null)
        assertEquals 2, model.paths.size()
        assertEquals "testPath1", model.paths.get(0)
        assertEquals "testPath2", model.paths.get(1)

        assertEquals 2, model.shortPaths.size()
        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)}", model.shortPaths.get(0)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)}", model.shortPaths.get(1)
    }

    void testSimpleQuery() {
        assertNotNull simulationRun
        initResults()

        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, createResultNodes(), false, true, true, null)
        model.criterias.clear()
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.GREATER_EQUALS
        criteriaViewModel.value = 4
        criteriaViewModel.valueInterpretationModel.selectedEnum = ValueInterpretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate"

        model.criterias.add([criteriaViewModel])

        model.query()

        assertEquals 2, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P0", model.resultTableModel.getColumnName(2)
        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P1", model.resultTableModel.getColumnName(3)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P1", model.resultTableModel.getColumnName(4)

        //assertEquals 3, model.resultTableModel.getValueAt(0, 0)
        assertEquals 4, model.resultTableModel.getValueAt(0, 1)
        assertEquals 12, model.resultTableModel.getValueAt(0, 2)
        assertEquals 8, model.resultTableModel.getValueAt(0, 3)
        assertEquals 24, model.resultTableModel.getValueAt(0, 4)

        //assertEquals 4, model.resultTableModel.getValueAt(1, 0)
        assertEquals 5, model.resultTableModel.getValueAt(1, 1)
        assertEquals 15, model.resultTableModel.getValueAt(1, 2)
        assertEquals 10, model.resultTableModel.getValueAt(1, 3)
        assertEquals 30, model.resultTableModel.getValueAt(1, 4)
    }

    void testSimpleQueryWithDifferentFields() {
        assertNotNull simulationRun
        initResultsWithDifferentFields()

        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, createResultNodes(), false, true, true, null)
        model.criterias.clear()
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.GREATER_EQUALS
        criteriaViewModel.value = 4
        criteriaViewModel.valueInterpretationModel.selectedEnum = ValueInterpretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString()

        model.criterias.add([criteriaViewModel])

        model.query()

        assertEquals 2, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P0", model.resultTableModel.getColumnName(2)
        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P1", model.resultTableModel.getColumnName(3)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P1", model.resultTableModel.getColumnName(4)

        //assertEquals 3, model.resultTableModel.getValueAt(0, 0)
        assertEquals 4, model.resultTableModel.getValueAt(0, 1)
        assertEquals 12, model.resultTableModel.getValueAt(0, 2)
        assertEquals 8, model.resultTableModel.getValueAt(0, 3)
        assertEquals 24, model.resultTableModel.getValueAt(0, 4)

        //assertEquals 4, model.resultTableModel.getValueAt(1, 0)
        assertEquals 5, model.resultTableModel.getValueAt(1, 1)
        assertEquals 15, model.resultTableModel.getValueAt(1, 2)
        assertEquals 10, model.resultTableModel.getValueAt(1, 3)
        assertEquals 30, model.resultTableModel.getValueAt(1, 4)
    }

    void testOrQuery() {
        assertNotNull simulationRun
        initResults()

        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, createResultNodes(), false, true, true, null)
        model.criterias.clear()
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.GREATER_EQUALS
        criteriaViewModel.value = 4
        criteriaViewModel.valueInterpretationModel.selectedEnum = ValueInterpretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate"

        model.criterias.add([criteriaViewModel])

        criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.LESS_EQUALS
        criteriaViewModel.value = 5
        criteriaViewModel.valueInterpretationModel.selectedEnum = ValueInterpretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate"

        model.criterias.get(0).add(criteriaViewModel)

        model.query()

        assertEquals 2, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P0", model.resultTableModel.getColumnName(2)
        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P1", model.resultTableModel.getColumnName(3)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P1", model.resultTableModel.getColumnName(4)

        //assertEquals 3, model.resultTableModel.getValueAt(0, 0)
        assertEquals 4, model.resultTableModel.getValueAt(0, 1)
        assertEquals 12, model.resultTableModel.getValueAt(0, 2)
        assertEquals 8, model.resultTableModel.getValueAt(0, 3)
        assertEquals 24, model.resultTableModel.getValueAt(0, 4)

        //assertEquals 4, model.resultTableModel.getValueAt(1, 0)
        assertEquals 5, model.resultTableModel.getValueAt(1, 1)
        assertEquals 15, model.resultTableModel.getValueAt(1, 2)
        assertEquals 10, model.resultTableModel.getValueAt(1, 3)
        assertEquals 30, model.resultTableModel.getValueAt(1, 4)
    }

    void testAndQuery() {
        assertNotNull simulationRun
        initResults()

        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, createResultNodes(), false, true, true, null)
        model.criterias.clear()

        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.GREATER_EQUALS
        criteriaViewModel.value = 5
        criteriaViewModel.valueInterpretationModel.selectedEnum = ValueInterpretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate"

        model.criterias.add([criteriaViewModel])

        criteriaViewModel = new CriteriaViewModel(model)
        criteriaViewModel.selectedPeriod = 0
        criteriaViewModel.selectedComparator = CriteriaComparator.LESS_EQUALS
        criteriaViewModel.value = 1
        criteriaViewModel.valueInterpretationModel.selectedEnum = ValueInterpretationType.ABSOLUTE
        criteriaViewModel.selectedPath = "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate"

        model.criterias.add([criteriaViewModel])

        model.query()

        assertEquals 2, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P0", model.resultTableModel.getColumnName(2)
        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P1", model.resultTableModel.getColumnName(3)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P1", model.resultTableModel.getColumnName(4)

        //assertEquals 0, model.resultTableModel.getValueAt(0, 0)
        assertEquals 1, model.resultTableModel.getValueAt(0, 1)
        assertEquals 3, model.resultTableModel.getValueAt(0, 2)
        assertEquals 2, model.resultTableModel.getValueAt(0, 3)
        assertEquals 6, model.resultTableModel.getValueAt(0, 4)

        //assertEquals 4, model.resultTableModel.getValueAt(1, 0)
        assertEquals 5, model.resultTableModel.getValueAt(1, 1)
        assertEquals 15, model.resultTableModel.getValueAt(1, 2)
        assertEquals 10, model.resultTableModel.getValueAt(1, 3)
        assertEquals 30, model.resultTableModel.getValueAt(1, 4)
    }

    void testEmptyQuery() {
        assertNotNull simulationRun
        initResults()

        ResultIterationDataViewModel model = new ResultIterationDataViewModel(simulationRun, createResultNodes(), false, true, true, null)
        model.criterias.clear()

        model.query()

        assertEquals simulationRun.iterations, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P0", model.resultTableModel.getColumnName(2)
        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P1", model.resultTableModel.getColumnName(3)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P1", model.resultTableModel.getColumnName(4)

        //assertEquals 1, model.resultTableModel.getValueAt(1, 0)
        assertEquals 2, model.resultTableModel.getValueAt(1, 1)
        assertEquals 6, model.resultTableModel.getValueAt(1, 2)
        assertEquals 4, model.resultTableModel.getValueAt(1, 3)
        assertEquals 12, model.resultTableModel.getValueAt(1, 4)

        //assertEquals 4, model.resultTableModel.getValueAt(4, 0)
        assertEquals 5, model.resultTableModel.getValueAt(4, 1)
        assertEquals 15, model.resultTableModel.getValueAt(4, 2)
        assertEquals 10, model.resultTableModel.getValueAt(4, 3)
        assertEquals 30, model.resultTableModel.getValueAt(4, 4)

        model.orderByPath = true
        model.query()

        assertEquals simulationRun.iterations, model.resultTableModel.rowCount
        assertEquals simulationRun.periodCount * 2 + 1, model.resultTableModel.columnCount

        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P0", model.resultTableModel.getColumnName(1)
        assertEquals "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate P1", model.resultTableModel.getColumnName(2)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P0", model.resultTableModel.getColumnName(3)
        assertEquals "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate P1", model.resultTableModel.getColumnName(4)

        //assertEquals 1, model.resultTableModel.getValueAt(1, 0)
        assertEquals 2, model.resultTableModel.getValueAt(1, 1)
        assertEquals 4, model.resultTableModel.getValueAt(1, 2)
        assertEquals 6, model.resultTableModel.getValueAt(1, 3)
        assertEquals 12, model.resultTableModel.getValueAt(1, 4)

        //assertEquals 4, model.resultTableModel.getValueAt(4, 0)
        assertEquals 5, model.resultTableModel.getValueAt(4, 1)
        assertEquals 10, model.resultTableModel.getValueAt(4, 2)
        assertEquals 15, model.resultTableModel.getValueAt(4, 3)
        assertEquals 30, model.resultTableModel.getValueAt(4, 4)


    }

    private void initResults() {
        simulationRun.iterations.times {int iteration ->
            simulationRun.periodCount.times {int period ->
                writeResult new SingleValueResult(simulationRun: simulationRun, period: period, iteration: (iteration + 1), valueIndex: 0, value: (iteration + 1) * (period + 1), field: field, path: path1, collector: collector)
                writeResult new SingleValueResult(simulationRun: simulationRun, period: period, iteration: (iteration + 1), valueIndex: 0, value: (iteration + 1) * (period + 1) * 3, field: field, path: path2, collector: collector)
            }
        }
    }

    private void initResultsWithDifferentFields() {
        simulationRun.iterations.times {int iteration ->
            simulationRun.periodCount.times {int period ->
                writeResult new SingleValueResult(simulationRun: simulationRun, period: period, iteration: (iteration + 1), valueIndex: 0, value: (iteration + 1) * (period + 1), field: field, path: path1, collector: collector)
                writeResult new SingleValueResult(simulationRun: simulationRun, period: period, iteration: (iteration + 1), valueIndex: 0, value: (iteration + 1) * (period + 1) * 3, field: field, path: path2, collector: collector)
                writeResult new SingleValueResult(simulationRun: simulationRun, period: period, iteration: (iteration + 1), valueIndex: 0, value: 5, field: field2, path: path1, collector: collector)
            }
        }
    }

    private List createResultNodes() {
        def res = []
        2.times {int i ->
            String testPath = "testPath${i + 1}"
            def parent = new SimpleTableTreeNode(testPath)
            def result = new ResultTableTreeNode("ultimate")
            result.resultPath = "$testPath:ultimate"
            result.collector = collector.collectorName
            parent.add(result)
            res << result
        }
        return res
    }

    private void writeResult(SingleValueResult result) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(result.iteration);
        dos.writeInt(1);
        dos.writeDouble(result.value);
        dos.writeLong(0);

        resultWriter.writeResult(new ResultTransferObject(new ResultDescriptor(result.field.id, result.path.id, collector.id, result.period), null, bos.toByteArray(), 0));
    }


}