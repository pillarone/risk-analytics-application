package org.pillarone.riskanalytics.application.ui.result.model

import models.deterministicApplication.DeterministicApplicationModel
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.fileimport.ResultStructureImportService
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.output.*
import org.joda.time.DateTime

class DeterministicResultViewModelTests extends GroovyTestCase {

    SimulationRun simulationRun
    PathMapping path1
    PathMapping path2
    FieldMapping field
    CollectorMapping collector1
    CollectorMapping collector2

    void setUp() {
        LocaleResources.setTestMode()
        ModellingItemFactory.clear()

        new ParameterizationImportService().compareFilesAndWriteToDB(['DeterministicApplicationParameters'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['DeterministicApplicationResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['DeterministicApplicationStructure'])
        ResultStructureImportService.importDefaults()
        simulationRun = new SimulationRun()
        simulationRun.name = "testRun"
        simulationRun.parameterization = ParameterizationDAO.findByName('DeterministicApplicationParameters')
        simulationRun.resultConfiguration = ResultConfigurationDAO.findByName('DeterministicApplicationResultConfiguration')
        simulationRun.model = DeterministicApplicationModel.name
        simulationRun.periodCount = 2
        simulationRun.iterations = 5
        simulationRun.randomSeed = 0
        simulationRun.beginOfFirstPeriod = new DateTime()

        simulationRun = simulationRun.save(flush: true)

        path1 = PathMapping.findByPathName("DeterministicApplication:composedComponent:subDynamicComponent:outValue1")
        if (path1 == null) {
            path1 = new PathMapping(pathName: 'testPath1').save()
        }

        path2 = PathMapping.findByPathName('DeterministicApplication:composedComponent:subDynamicComponent:subComponent:outFirstValue')
        if (path2 == null) {
            path2 = new PathMapping(pathName: 'testPath2').save()
        }

        field = FieldMapping.findByFieldName('value')
        if (field == null) {
            field = new FieldMapping(fieldName: 'value').save()
        }

        collector1 = CollectorMapping.findByCollectorName(AggregatedCollectingModeStrategy.IDENTIFIER)
        if (collector1 == null) {
            collector1 = new CollectorMapping(collectorName: AggregatedCollectingModeStrategy.IDENTIFIER).save()
        }

        collector2 = CollectorMapping.findByCollectorName(SingleValueCollectingModeStrategy.IDENTIFIER)
        if (collector2 == null) {
            collector2 = new CollectorMapping(collectorName: SingleValueCollectingModeStrategy.IDENTIFIER).save()
        }
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testPaths() {
        assertNotNull new PostSimulationCalculation(run: simulationRun, period: 0, path: path1, collector: collector1, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        assertNotNull new PostSimulationCalculation(run: simulationRun, period: 2, path: path2, collector: collector2, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        Simulation simulation = new Simulation("testRun")
        simulation.load()

        Model model = new DeterministicApplicationModel()

        AbstractResultViewModel resultViewModel = new DeterministicResultViewModel(model, ModelStructure.getStructureForModel(model.class), simulation)
        assertEquals 2, resultViewModel.builder.allPaths.size()
        assertTrue resultViewModel.builder.allPaths.keySet().contains(path1.pathName + ":" + field.fieldName)
        assertTrue resultViewModel.builder.allPaths.keySet().contains(path2.pathName + ":" + field.fieldName)

        assertEquals AggregatedCollectingModeStrategy.IDENTIFIER, resultViewModel.builder.allPaths.get(path1.pathName + ":" + field.fieldName).getIdentifier()
        assertEquals SingleValueCollectingModeStrategy.IDENTIFIER, resultViewModel.builder.allPaths.get(path2.pathName + ":" + field.fieldName).getIdentifier()
    }

}
