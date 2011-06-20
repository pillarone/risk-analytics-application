package org.pillarone.riskanalytics.application.ui.main.view.item

import models.core.CoreModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
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
import org.springframework.transaction.TransactionStatus

/**
 * @author fouad.jaada@intuitive-collaboration.com
 *
 */
class BatchUIItemTests extends AbstractUIItemTest {

    @Override
    AbstractUIItem createUIItem() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Core"])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(["Core"])
        LocaleResources.setTestMode()
        SimulationRun run
        BatchRun batchRun = null
        BatchRunSimulationRun.withTransaction {TransactionStatus status ->
            batchRun = new BatchRun(name: "test", executionTime: new DateTime())
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
        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel()
        BatchUIItem batchUIItem = new BatchUIItem(mainModel, batchRun)
        return batchUIItem
    }


    public void testView() {
        //todo fja
        //        assertEquals 1, tableOperator.rowCount
        //        assertEquals 8, tableOperator.columnCount
        //
        //        assertEquals "run", tableOperator.getValueAt(0, 0)
        //        assertEquals "CoreModel", tableOperator.getValueAt(0, 1)
        //        assertEquals "CoreAlternativeParameters v1", tableOperator.getValueAt(0, 2)
        //        assertEquals "CoreResultConfiguration v1", tableOperator.getValueAt(0, 3)
        //        assertEquals "2/5", tableOperator.getValueAt(0, 4)
        //        assertEquals 0, tableOperator.getValueAt(0, 5)
        //        assertEquals "No output", tableOperator.getValueAt(0, 6)
        //        assertEquals "not running", tableOperator.getValueAt(0, 7)
    }


}
