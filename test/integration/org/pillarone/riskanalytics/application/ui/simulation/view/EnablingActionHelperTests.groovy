package org.pillarone.riskanalytics.application.ui.simulation.view

import models.core.CoreModel
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.simulation.model.SimulationConfigurationModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.simulation.SimulationState

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class EnablingActionHelperTests extends GroovyTestCase {
    SimulationConfigurationModel model
    EnablingActionHelper enablingActionHelper

    void setUp() {
        LocaleResources.setTestMode()
        FileImportService.importModelsIfNeeded(['Core'])
        model = new SimulationConfigurationModel(new P1RATModel(), CoreModel, null, null)
        enablingActionHelper = new EnablingActionHelper(model)

        setValidParameterization()
    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
    }


    public void testNotRunning() {
        model.runner.simulationState = SimulationState.NOT_RUNNING
        assertTrue !enablingActionHelper.isRunButtonEnabled()
        assertTrue !enablingActionHelper.isOpenResultButtonEnabled()
        assertTrue !enablingActionHelper.isStopButtonEnabled()
        assertTrue !enablingActionHelper.isCancelButtonEnabled()
        assertTrue enablingActionHelper.isAddToBatchButtonEnabled()
    }

    public void testInitializing() {
        model.runner.simulationState = SimulationState.INITIALIZING
        assertTrue !enablingActionHelper.isRunButtonEnabled()
        assertTrue !enablingActionHelper.isOpenResultButtonEnabled()
        assertTrue enablingActionHelper.isStopButtonEnabled()
        assertTrue enablingActionHelper.isCancelButtonEnabled()
        assertTrue !enablingActionHelper.isAddToBatchButtonEnabled()
    }

    public void testRunning() {
        model.runner.simulationState = SimulationState.RUNNING
        assertTrue !enablingActionHelper.isRunButtonEnabled()
        assertTrue !enablingActionHelper.isOpenResultButtonEnabled()
        assertTrue enablingActionHelper.isStopButtonEnabled()
        assertTrue enablingActionHelper.isCancelButtonEnabled()
        assertTrue !enablingActionHelper.isAddToBatchButtonEnabled()
    }

    public void testSavingResults() {
        model.runner.simulationState = SimulationState.SAVING_RESULTS
        assertTrue !enablingActionHelper.isRunButtonEnabled()
        assertTrue !enablingActionHelper.isOpenResultButtonEnabled()
        assertTrue enablingActionHelper.isStopButtonEnabled()
        assertTrue enablingActionHelper.isCancelButtonEnabled()
        assertTrue !enablingActionHelper.isAddToBatchButtonEnabled()
    }

    public void testPostSimulationCalculation() {
        model.runner.simulationState = SimulationState.POST_SIMULATION_CALCULATIONS
        assertTrue !enablingActionHelper.isRunButtonEnabled()
        assertTrue !enablingActionHelper.isOpenResultButtonEnabled()
        assertTrue enablingActionHelper.isStopButtonEnabled()
        assertTrue enablingActionHelper.isCancelButtonEnabled()
        assertTrue !enablingActionHelper.isAddToBatchButtonEnabled()
    }


    public void testStopped() {
        model.runner.simulationState = SimulationState.POST_SIMULATION_CALCULATIONS
        model.stopAction.clicked = true
        assertTrue !enablingActionHelper.isRunButtonEnabled()
        assertTrue !enablingActionHelper.isOpenResultButtonEnabled()
        assertTrue !enablingActionHelper.isStopButtonEnabled()
        assertTrue enablingActionHelper.isCancelButtonEnabled()
        assertTrue !enablingActionHelper.isAddToBatchButtonEnabled()

    }

    public void testCanceled() {
        model.runner.simulationState = SimulationState.CANCELED
        assertTrue !enablingActionHelper.isRunButtonEnabled()
        assertTrue !enablingActionHelper.isOpenResultButtonEnabled()
        assertTrue !enablingActionHelper.isStopButtonEnabled()
        assertTrue !enablingActionHelper.isCancelButtonEnabled()
        assertTrue enablingActionHelper.isAddToBatchButtonEnabled()
    }


    private def setValidParameterization() {
        ParameterizationDAO parameterizationDAO = ParameterizationDAO.findByName('CoreParameters')
        parameterizationDAO.valid = true
        parameterizationDAO.save()
    }
}
