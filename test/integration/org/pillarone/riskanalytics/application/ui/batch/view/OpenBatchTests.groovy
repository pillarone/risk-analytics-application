package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.testframework.operator.ULCTableOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import javax.swing.tree.TreePath
import models.core.CoreModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.output.OutputStrategy
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase
import org.springframework.transaction.TransactionStatus

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class OpenBatchTests extends AbstractFunctionalTestCase {


    protected void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Core"])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(["Core"])
        LocaleResources.setTestMode()
        SimulationRun run
        BatchRunSimulationRun.withTransaction {TransactionStatus status ->
            BatchRun batchRun = new BatchRun(name: "test", executionTime: new DateTime())
            batchRun.save(flush: true)
            ParameterizationDAO dao = ParameterizationDAO.list()[0]
            ResultConfigurationDAO configurationDAO = ResultConfigurationDAO.list()[0]
            run = new SimulationRun(name: "run")
            run.parameterization = dao
            run.resultConfiguration = configurationDAO
            run.model = CoreModel.name
            run.periodCount = 2
            run.iterations = 5
            run.randomSeed = 0
            run.save(flush: true)

            BatchRunSimulationRun batchRunSimulationRun = new BatchRunSimulationRun()
            batchRunSimulationRun.batchRun = BatchRun.findByName(batchRun.name)//batchRun
            batchRunSimulationRun.simulationRun = SimulationRun.findByName(run.name)
            batchRunSimulationRun.priority = 0
            batchRunSimulationRun.strategy = OutputStrategy.NO_OUTPUT
            batchRunSimulationRun.simulationState = SimulationState.NOT_RUNNING
            batchRunSimulationRun.save(flush: true)
        }

        super.setUp();

    }

    @Override protected void tearDown() {
        LocaleResources.clearTestMode()
        super.tearDown()
    }

    public void testOpenBatch() {
        ULCTableTreeOperator tableTree = getSelectionTableTreeRowHeader()
        int batchRow = tableTree.findRow("Batches")

        tableTree.doExpandRow batchRow
        tableTree.selectCell(batchRow + 1, 0)
        tableTree.pushKey(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK)
        ULCTableOperator tableOperator = getTableOperator("batchesTable")
        assertEquals 1, tableOperator.rowCount
        assertEquals 7, tableOperator.columnCount

        assertEquals "run", tableOperator.getValueAt(0, 0)
        assertEquals "CoreModel", tableOperator.getValueAt(0, 1)
        assertEquals "CoreAlternativeParameters v1", tableOperator.getValueAt(0, 2)
        assertEquals "CoreResultConfiguration v1", tableOperator.getValueAt(0, 3)
        assertEquals "2/5", tableOperator.getValueAt(0, 4)
        assertEquals "No output", tableOperator.getValueAt(0, 5)
        assertEquals "not running", tableOperator.getValueAt(0, 6)

    }
}
