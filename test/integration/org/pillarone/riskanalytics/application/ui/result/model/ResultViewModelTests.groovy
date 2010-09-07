package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.core.output.PathMapping
import models.application.ApplicationModel
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService


class ResultViewModelTests extends GroovyTestCase {

    SimulationRun simulationRun
    PathMapping path1
    PathMapping path2
    FieldMapping field
    FieldMapping field2
    CollectorMapping collector

    void setUp() {
        LocaleResources.setTestMode()

        new ParameterizationImportService().compareFilesAndWriteToDB(['ApplicationParameters'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['ApplicationResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['ApplicationStructure'])
        simulationRun = new SimulationRun()
        simulationRun.name = "testRun"
        simulationRun.parameterization = ParameterizationDAO.findByName('ApplicationParameters')
        simulationRun.resultConfiguration = ResultConfigurationDAO.findByName('ApplicationResultConfiguration')
        simulationRun.model = ApplicationModel.name
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

    void testPaths() {
        assertNotNull new PostSimulationCalculation(run: simulationRun, period: 0, path: path1, collector: collector, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        assertNotNull new PostSimulationCalculation(run: simulationRun, period: 2, path: path2, collector: collector, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        Simulation simulation = new Simulation("testRun")
        simulation.load()

        Model model = new ApplicationModel()

        ResultViewModel resultViewModel = new ResultViewModel(model, ModelStructure.getStructureForModel(model.class), simulation)
        assertEquals 2, resultViewModel.builder.allPaths.size()
        assertTrue resultViewModel.builder.allPaths.contains(path1.pathName)
        assertTrue resultViewModel.builder.allPaths.contains(path2.pathName)
    }
}