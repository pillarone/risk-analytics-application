package org.pillarone.riskanalytics.application.ui.result.model

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.dataaccess.function.MaxFunction
import org.pillarone.riskanalytics.application.dataaccess.function.MinFunction
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
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory
import org.pillarone.riskanalytics.application.dataaccess.function.MeanFunction

class ResultViewModelTests extends GroovyTestCase {

    SimulationRun simulationRun
    PathMapping path1
    PathMapping path2
    FieldMapping field
    CollectorMapping collector1
    CollectorMapping collector2

    void setUp() {
        LocaleResources.setTestMode()
        ModellingItemFactory.clear()

        new ParameterizationImportService().compareFilesAndWriteToDB(['ApplicationParameters'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['ApplicationResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['ApplicationStructure'])
        new ResultStructureImportService().compareFilesAndWriteToDB(['ApplicationDefaultResultTree'])
        new ResultStructureImportService().compareFilesAndWriteToDB(['ApplicationAlternativeResultTree'])
        simulationRun = new SimulationRun()
        simulationRun.name = "testRun"
        simulationRun.parameterization = ParameterizationDAO.findByName('ApplicationParameters')
        simulationRun.resultConfiguration = ResultConfigurationDAO.findByName('ApplicationResultConfiguration')
        simulationRun.model = ApplicationModel.name
        simulationRun.periodCount = 2
        simulationRun.iterations = 5
        simulationRun.randomSeed = 0

        simulationRun = simulationRun.save(flush: true)

        path1 = PathMapping.findByPathName("Application:composedComponent:subDynamicComponent:outValue1")
        if (path1 == null) {
            path1 = new PathMapping(pathName: 'testPath1').save()
        }

        path2 = PathMapping.findByPathName('Application:composedComponent:subDynamicComponent:subComponent:outFirstValue')
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

    void testSelectedResultStructure() {
        assertNotNull new PostSimulationCalculation(run: simulationRun, period: 0, path: path1, collector: collector1, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        assertNotNull new PostSimulationCalculation(run: simulationRun, period: 2, path: path2, collector: collector2, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        Simulation simulation = new Simulation("testRun")
        simulation.load()

        Model model = new ApplicationModel()

        List<ResultStructureDAO> structures = ResultStructureDAO.findAllByModelClassName(ApplicationModel.name)
        assertEquals(2, structures.size())

        UserPreferences preferences = UserPreferencesFactory.getUserPreferences()
        preferences.putPropertyValue("DEFAULT_VIEW" + model.name, structures[0].name)

        ResultViewModel resultViewModel = new ResultViewModel(model, ModelStructure.getStructureForModel(model.class), simulation)
        assertEquals(2, resultViewModel.resultStructures.size())
        Object selectedStructure = resultViewModel.selectionViewModel.selectedObject
        assertEquals(selectedStructure.name, structures[0].name)
        assertSame(selectedStructure, resultViewModel.builder.resultStructure)


        preferences.putPropertyValue("DEFAULT_VIEW" + model.name, structures[1].name)

        resultViewModel = new ResultViewModel(model, ModelStructure.getStructureForModel(model.class), simulation)
        assertEquals(2, resultViewModel.resultStructures.size())
        selectedStructure = resultViewModel.selectionViewModel.selectedObject
        assertEquals(selectedStructure.name, structures[1].name)
        assertSame(selectedStructure, resultViewModel.builder.resultStructure)
    }

    void testPaths() {
        assertNotNull new PostSimulationCalculation(run: simulationRun, period: 0, path: path1, collector: collector1, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        assertNotNull new PostSimulationCalculation(run: simulationRun, period: 2, path: path2, collector: collector2, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        Simulation simulation = new Simulation("testRun")
        simulation.load()

        Model model = new ApplicationModel()

        ResultViewModel resultViewModel = new ResultViewModel(model, ModelStructure.getStructureForModel(model.class), simulation)
        assertEquals 2, resultViewModel.builder.allPaths.size()
        assertTrue resultViewModel.builder.allPaths.keySet().contains(path1.pathName + ":" + field.fieldName)
        assertTrue resultViewModel.builder.allPaths.keySet().contains(path2.pathName + ":" + field.fieldName)

        assertEquals AggregatedCollectingModeStrategy.IDENTIFIER, resultViewModel.builder.allPaths.get(path1.pathName + ":" + field.fieldName).getIdentifier()
        assertEquals SingleValueCollectingModeStrategy.IDENTIFIER, resultViewModel.builder.allPaths.get(path2.pathName + ":" + field.fieldName).getIdentifier()
    }

    void testFunctions() {
        assertNotNull new PostSimulationCalculation(run: simulationRun, period: 0, path: path1, collector: collector1, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        assertNotNull new PostSimulationCalculation(run: simulationRun, period: 2, path: path2, collector: collector2, field: field, result: 0, keyFigure: PostSimulationCalculation.MEAN).save()
        Simulation simulation = new Simulation("testRun")
        simulation.load()

        Model model = new ApplicationModel()

        ResultViewModel resultViewModel = new ResultViewModel(model, ModelStructure.getStructureForModel(model.class), simulation)

        MaxFunction max = new MaxFunction()
        MinFunction min = new MinFunction()

        def treeModel = resultViewModel.treeModel
        resultViewModel.addFunction(new MeanFunction())
        assertEquals 3, treeModel.functions.size() //node name + 2 * mean

        resultViewModel.addFunction(max)
        assertEquals 5, treeModel.functions.size()
        assertSame max, treeModel.functions[3]
        assertSame max, treeModel.functions[4]

        resultViewModel.addFunction(min)
        assertEquals 7, treeModel.functions.size()
        assertSame min, treeModel.functions[5]
        assertSame min, treeModel.functions[6]

        resultViewModel.removeFunction(max)
        assertEquals 7, treeModel.functions.size()
        assertSame null, treeModel.functions[3]
        assertSame null, treeModel.functions[4]

        resultViewModel.addFunction(max)
        assertEquals 9, treeModel.functions.size()
        assertSame max, treeModel.functions[7]
        assertSame max, treeModel.functions[8]
    }
}
