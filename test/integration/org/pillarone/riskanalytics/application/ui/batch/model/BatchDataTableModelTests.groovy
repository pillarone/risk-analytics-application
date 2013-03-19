package org.pillarone.riskanalytics.application.ui.batch.model

import models.core.CoreModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.output.OutputStrategy
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.SimulationState

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class BatchDataTableModelTests extends GroovyTestCase {

    public void testModel() {
        BatchDataTableModel model = new BatchDataTableModel(new BatchRun(name: "test"))
        model.metaClass.init = {->
            BatchRunSimulationRun batchRunSimulationRun = getBatchRunSimulationRun(model)
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

    BatchRunSimulationRun getBatchRunSimulationRun(BatchDataTableModel model) {

        BatchRun batchRun = new BatchRun(name: "test", executionTime: new DateTime())
//        batchRun.save(flush: true)
        ParameterizationDAO dao = new ParameterizationDAO(name: "ParameterizationDAO")
        dao.itemVersion = "1.1"
        ResultConfigurationDAO configurationDAO = new ResultConfigurationDAO(name: "ResultConfigurationDAO")
        configurationDAO.itemVersion = "2.0"
        SimulationRun run = new SimulationRun(name: "run")
        run.parameterization = dao
        run.resultConfiguration = configurationDAO
        run.model = CoreModel.name
        run.periodCount = 2
        run.iterations = 5
        run.randomSeed = 1234
//        run.save(flush: true)

        BatchRunSimulationRun batchRunSimulationRun = new BatchRunSimulationRun()
        batchRunSimulationRun.batchRun = batchRun
        batchRunSimulationRun.simulationRun = run
        batchRunSimulationRun.priority = 0
        batchRunSimulationRun.strategy = OutputStrategy.NO_OUTPUT
        batchRunSimulationRun.simulationState = SimulationState.NOT_RUNNING
        return batchRunSimulationRun
    }
}
