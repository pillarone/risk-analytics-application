package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.ULCFrame
import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.fileimport.ResultStructureImportService
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractSimulationCommentTests extends AbstractSimpleFunctionalTest {

    Simulation simulation
    ResultViewModel resultViewModel

    @Override
    protected void doStart() {

        new ParameterizationImportService().compareFilesAndWriteToDB(['ApplicationParameters'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['ApplicationResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['ApplicationStructure'])
        new ResultStructureImportService().compareFilesAndWriteToDB(['ApplicationDefaultResultTree'])
        SimulationRun simulationRun = new SimulationRun()
        simulationRun.name = "testRun"
        simulationRun.parameterization = ParameterizationDAO.findByName('ApplicationParameters')
        simulationRun.resultConfiguration = ResultConfigurationDAO.findByName('ApplicationResultConfiguration')
        simulationRun.model = ApplicationModel.name
        simulationRun.periodCount = 2
        simulationRun.iterations = 5
        simulationRun.randomSeed = 0

        simulationRun.save(flush: true)

        simulation = new Simulation("testRun")
        simulation.load()

        Model model = new ApplicationModel()
        resultViewModel = new ResultViewModel(model, ModelStructure.getStructureForModel(model.class), simulation)



        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"
        frame.setContentPane(createContent())
        frame.visible = true

    }

    abstract ULCContainer createContent()
}
