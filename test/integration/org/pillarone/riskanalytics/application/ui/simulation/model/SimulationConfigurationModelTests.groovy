package org.pillarone.riskanalytics.application.ui.simulation.model

import models.core.CoreModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.SimulationState

class SimulationConfigurationModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
        FileImportService.importModelsIfNeeded(['Core'])

        setValidParameterization()
    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testModelInitialization() {
        SimulationConfigurationModel model = new SimulationConfigurationModel(new P1RATModel(), CoreModel, null, null)
        assertNull model.currentSimulation

        assertFalse model.runAction.enabled
        assertFalse model.stopAction.enabled
        assertFalse model.openResultAction.enabled

        model.iterationCount = 1000

        assertTrue model.runAction.enabled
        assertFalse model.stopAction.enabled
        assertFalse model.openResultAction.enabled

        model.simulationName = 'simulation'

        assertEquals SimulationState.NOT_RUNNING, model.runner.simulationState

        assertEquals "SimulationNotRunningMessage", model.simulationMessage
    }

    void testStartStop() {
        StochasticModel simulationModel = new CoreModel()
        SimulationConfigurationModel model = new SimulationConfigurationModel(new P1RATModel(), CoreModel, null, null)
        model.iterationCount = 100
        model.simulationName = 'Simulation'
        model.runSimulation()

        def runner = model.runner
        assertNotNull runner

        model.stopSimulation()
        assertEquals "Stopped", model.simulationMessage


    }

    private def setValidParameterization() {
        ParameterizationDAO parameterizationDAO = ParameterizationDAO.findByName('CoreParameters')
        parameterizationDAO.valid = true
        parameterizationDAO.save()
    }


}