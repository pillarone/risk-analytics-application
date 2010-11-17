package org.pillarone.riskanalytics.application.ui.result.model

import models.core.CoreModel
import org.jfree.chart.JFreeChart
import org.jfree.data.category.DefaultCategoryDataset
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.chart.model.ParallelCoordinatesChartViewModel
import org.pillarone.riskanalytics.application.util.LocaleResources
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
import org.pillarone.riskanalytics.application.ui.chart.model.ParallelCoordinatesChartViewModel
import org.pillarone.riskanalytics.core.output.batch.AbstractBulkInsert
import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultTransferObject
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultDescriptor
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultWriter

class ParallelCoordinatesChartViewModelTests extends GroovyTestCase {

    SimulationRun simulationRun
    PathMapping path1
    PathMapping path2
    FieldMapping field
    FieldMapping field2
    CollectorMapping collector

    private ResultWriter resultWriter

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

    protected void tearDown() {
        LocaleResources.clearTestMode()
        super.tearDown();
    }



    void testDataset() {
        assertNotNull simulationRun
        initResults()

        ParallelCoordinatesChartViewModel model = new ParallelCoordinatesChartViewModel("title", simulationRun, createResultNodes(), false)
        model.queryPaneModel.query()
        JFreeChart chart = model.getChart()

        DefaultCategoryDataset dataset = chart.plot.datasets.get(0)
        assertEquals 1, dataset.getValue(0, "test path1 / ultimate")
        assertEquals 2, dataset.getValue(1, "test path1 / ultimate")
        assertEquals 3, dataset.getValue(2, "test path1 / ultimate")
        assertEquals 4, dataset.getValue(3, "test path1 / ultimate")
        assertEquals 5, dataset.getValue(4, "test path1 / ultimate")
        assertEquals 6, dataset.getValue(5, "test path1 / ultimate")

        assertEquals 6, dataset.getValue(1, "test path2 / ultimate")
        assertEquals 9, dataset.getValue(2, "test path2 / ultimate")
        assertEquals 12, dataset.getValue(3, "test path2 / ultimate")
        assertEquals 15, dataset.getValue(4, "test path2 / ultimate")
        assertEquals 18, dataset.getValue(5, "test path2 / ultimate")

        model.setPeriodVisibility(1, true)
        chart = model.getChart()
        dataset = chart.plot.datasets.get(0)

        assertEquals 4, dataset.getValue(1, "test path1 / ultimate")
        assertEquals 6, dataset.getValue(2, "test path1 / ultimate")
        assertEquals 8, dataset.getValue(3, "test path1 / ultimate")
        assertEquals 10, dataset.getValue(4, "test path1 / ultimate")
        assertEquals 12, dataset.getValue(5, "test path1 / ultimate")
        println "4: ${dataset.getValue(4, "test path1 / ultimate")}"
        println "5: ${dataset.getValue(5, "test path1 / ultimate")}"

//        assertEquals 6, dataset.getValue(0, "test path2 / ultimate")
        assertEquals 12, dataset.getValue(1, "test path2 / ultimate")
        assertEquals 18, dataset.getValue(2, "test path2 / ultimate")
        assertEquals 24, dataset.getValue(3, "test path2 / ultimate")
        assertEquals 30, dataset.getValue(4, "test path2 / ultimate")
        assertEquals 36, dataset.getValue(5, "test path2 / ultimate")
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
        dos.writeDouble(result.value);

        resultWriter.writeResult(new ResultTransferObject(new ResultDescriptor(result.field.id, result.path.id, result.period), null, bos.toByteArray(), 0));
    }

    private List createResultNodes() {
        def res = []
        (2..1).each {int i ->
            String testPath = "testPath${i}"
            def parent = new SimpleTableTreeNode(testPath)
            def result = new ResultTableTreeNode("ultimate")
            result.resultPath = "$testPath:ultimate"
            parent.add(result)
            res << result
        }
        return res
    }


}