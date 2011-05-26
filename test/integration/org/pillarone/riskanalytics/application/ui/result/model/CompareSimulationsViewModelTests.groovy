package org.pillarone.riskanalytics.application.ui.result.model

import models.application.ApplicationModel
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
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */

class CompareSimulationsViewModelTests extends GroovyTestCase {

    SimulationRun simulationRun1
    SimulationRun simulationRun2
    PathMapping path1
    FieldMapping field
    CollectorMapping collector1

    void setUp() {
        LocaleResources.setTestMode()
        ModellingItemFactory.clear()

        new ParameterizationImportService().compareFilesAndWriteToDB(['ApplicationParameters'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['ApplicationResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['ApplicationStructure'])
        new ResultStructureImportService().compareFilesAndWriteToDB(['ApplicationDefaultResultTree'])

        initSimulations()

        path1 = PathMapping.findByPathName('testPath1')
        if (path1 == null) {
            path1 = new PathMapping(pathName: 'testPath1').save()
        }

        collector1 = CollectorMapping.findByCollectorName(AggregatedCollectingModeStrategy.IDENTIFIER)
        if (collector1 == null) {
            collector1 = new CollectorMapping(collectorName: AggregatedCollectingModeStrategy.IDENTIFIER).save()
        }


        field = FieldMapping.findByFieldName('Ultimate')
        if (field == null) {
            field = new FieldMapping(fieldName: 'ultimate').save()
        }

    }


    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testPaths() {
        assertNotNull new PostSimulationCalculation(run: simulationRun1, period: 0, path: path1, collector: collector1, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        assertNotNull new PostSimulationCalculation(run: simulationRun2, period: 0, path: path1, collector: collector1, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        Simulation simulation1 = new Simulation("testRun1")
        simulation1.load()
        Simulation simulation2 = new Simulation("testRun2")
        simulation2.load()

        Model model = new ApplicationModel()

        CompareSimulationsViewModel compareSimulationsViewModel = new TestCompareSimulationsViewModel(model, ModelStructure.getStructureForModel(model.class), [simulation1, simulation2])
        assertEquals 1, compareSimulationsViewModel.builder.allPaths.size()
        assertTrue compareSimulationsViewModel.builder.allPaths.keySet().contains(path1.pathName + ":" + field.fieldName)

        assertEquals AggregatedCollectingModeStrategy.IDENTIFIER, compareSimulationsViewModel.builder.allPaths.get(path1.pathName + ":" + field.fieldName).getIdentifier()
    }

    private def initSimulations() {
        simulationRun1 = new SimulationRun()
        simulationRun1.name = "testRun1"
        simulationRun1.parameterization = ParameterizationDAO.findByName('ApplicationParameters')
        simulationRun1.resultConfiguration = ResultConfigurationDAO.findByName('ApplicationResultConfiguration')
        simulationRun1.model = ApplicationModel.name
        simulationRun1.periodCount = 2
        simulationRun1.iterations = 5
        simulationRun1.randomSeed = 0

        simulationRun1 = simulationRun1.save(flush: true)

        simulationRun2 = new SimulationRun()
        simulationRun2.name = "testRun2"
        simulationRun2.parameterization = ParameterizationDAO.findByName('ApplicationParameters')
        simulationRun2.resultConfiguration = ResultConfigurationDAO.findByName('ApplicationResultConfiguration')
        simulationRun2.model = ApplicationModel.name
        simulationRun2.periodCount = 2
        simulationRun2.iterations = 5
        simulationRun2.randomSeed = 0

        simulationRun2 = simulationRun2.save(flush: true)
    }


}
