package org.pillarone.riskanalytics.application.ui.result.model

import models.core.CoreModel
import org.jfree.chart.JFreeChart
import org.jfree.data.category.DefaultCategoryDataset
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.chart.model.ParallelCoordinatesChartViewModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.components.ComponentUtils
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.output.*
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultDescriptor
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultTransferObject
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultWriter

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class ParallelCoordinatesChartViewModelTests  {

    SimulationRun simulationRun
    PathMapping path1
    PathMapping path2
    FieldMapping field
    FieldMapping field2
    CollectorMapping collector

    private ResultWriter resultWriter

    @Before
    void setUp() {
        ResultAccessor.clearCaches()
        LocaleResources.setTestMode()

        new ParameterizationImportService().compareFilesAndWriteToDB(['Core'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['Core'])
        simulationRun = new SimulationRun()
        simulationRun.name = "testRun"
        simulationRun.parameterization = ParameterizationDAO.findByName('CoreParameters')
        simulationRun.resultConfiguration = ResultConfigurationDAO.findByName('CoreResultConfiguration')
        simulationRun.model = CoreModel.name
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

        field = FieldMapping.findByFieldName('ultimate')
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

    @After
    void tearDown() {
        LocaleResources.clearTestMode()
    }

    @Test
    void testDataset() {
        assertNotNull simulationRun
        initResults()

        ParallelCoordinatesChartViewModel model = new ParallelCoordinatesChartViewModel("title", simulationRun, createResultNodes(), false)
        model.queryPaneModel.query()
        JFreeChart chart = model.getChart()

        DefaultCategoryDataset dataset = chart.plot.datasets.get(0)
        assertEquals 2, dataset.getValue(1, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())
        assertEquals 3, dataset.getValue(2, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())
        assertEquals 4, dataset.getValue(3, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())
        assertEquals 5, dataset.getValue(4, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())
        assertEquals 6, dataset.getValue(5, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())

        assertEquals 6, dataset.getValue(1, "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate".toString())
        assertEquals 9, dataset.getValue(2, "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate".toString())
        assertEquals 12, dataset.getValue(3, "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate".toString())
        assertEquals 15, dataset.getValue(4, "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate".toString())
        assertEquals 18, dataset.getValue(5, "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate".toString())

        model.setPeriodVisibility(1, true)
        chart = model.getChart()
        dataset = chart.plot.datasets.get(0)

        assertEquals 4, dataset.getValue(1, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())
        assertEquals 6, dataset.getValue(2, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())
        assertEquals 8, dataset.getValue(3, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())
        assertEquals 10, dataset.getValue(4, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())
        assertEquals 12, dataset.getValue(5, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())
        println "4: ${dataset.getValue(4, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())}"
        println "5: ${dataset.getValue(5, "${ComponentUtils.getNormalizedName(path1.pathName)} / ultimate".toString())}"

//        assertEquals 6, dataset.getValue(0, "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate".toString())
        assertEquals 12, dataset.getValue(1, "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate".toString())
        assertEquals 18, dataset.getValue(2, "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate".toString())
        assertEquals 24, dataset.getValue(3, "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate".toString())
        assertEquals 30, dataset.getValue(4, "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate".toString())
        assertEquals 36, dataset.getValue(5, "${ComponentUtils.getNormalizedName(path2.pathName)} / ultimate".toString())
    }

    private void initResults() {
        simulationRun.iterations.times {int iteration ->
            iteration = iteration + 1
            simulationRun.periodCount.times {int period ->
                writeResult new SingleValueResult(simulationRun: simulationRun, period: period, iteration: iteration, valueIndex: 0, value: (iteration + 1) * (period + 1), field: field, path: path1, collector: collector)
                writeResult new SingleValueResult(simulationRun: simulationRun, period: period, iteration: iteration, valueIndex: 0, value: (iteration + 1) * (period + 1) * 3, field: field, path: path2, collector: collector)
            }
        }
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

    private List createResultNodes() {
        def res = []
        (2..1).each {int i ->
            String testPath = "testPath${i}"
            def parent = new SimpleTableTreeNode(testPath)
            def result = new ResultTableTreeNode("ultimate")
            result.resultPath = "$testPath:ultimate"
            result.collector = collector.collectorName
            parent.add(result)
            res << result
        }
        return res
    }


}