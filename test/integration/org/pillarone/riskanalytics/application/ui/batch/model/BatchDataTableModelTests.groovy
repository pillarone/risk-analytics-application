package org.pillarone.riskanalytics.application.ui.batch.model

import models.core.CoreModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.item.*

import static org.junit.Assert.assertEquals

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class BatchDataTableModelTests {

    @Before
    void setUp() throws Exception {
        LocaleResources.testMode = true
    }

    @After
    void tearDown() throws Exception {
        LocaleResources.testMode = false
    }

    @Test
    public void testModel() {
        BatchDataTableModel model = new BatchDataTableModel(new Batch("test"))
        model.metaClass.init = { ->
            Simulation batchRunSimulationRun = simulation
            model.tableValues << model.toList(batchRunSimulationRun)
        }
        model.init()

        assertEquals 8, model.columnCount
        assertEquals 1, model.rowCount

        assertEquals "run", model.getValueAt(0, 0)
        assertEquals "CoreModel", model.getValueAt(0, 1)
        assertEquals "ParameterizationDAO v1.1", model.getValueAt(0, 2)
        assertEquals "ResultConfigurationDAO v2.0", model.getValueAt(0, 3)
        assertEquals "2/5", model.getValueAt(0, 4)
        assertEquals 1234, model.getValueAt(0, 5)
    }

    Simulation getSimulation() {
        Simulation run = new Simulation("run")
        run.parameterization = new Parameterization('ParameterizationDAO')
        run.parameterization.versionNumber = new VersionNumber('1.1')
        run.template = new ResultConfiguration('ResultConfigurationDAO', CoreModel)
        run.template.versionNumber = new VersionNumber('2.0')
        run.modelClass = CoreModel
        run.periodCount = 2
        run.numberOfIterations = 5
        run.randomSeed = 1234
        run.simulationState = SimulationState.NOT_RUNNING
        return run
    }
}
