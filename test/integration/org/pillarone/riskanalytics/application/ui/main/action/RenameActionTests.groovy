package org.pillarone.riskanalytics.application.ui.main.action

import models.core.CoreModel
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RenameActionTests extends GroovyTestCase {
    @Override
    void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreParameters'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['CoreResultConfiguration'])
        LocaleResources.setTestMode()

    }

    @Override protected void tearDown() {
        LocaleResources.clearTestMode()
        super.tearDown()
    }



    public void testIsUsedInSimulation() {
        RenameAction renameAction = new RenameAction(null, null)
        ParameterizationDAO parameterizationDAO1 = ParameterizationDAO.list()[0]
        ResultConfigurationDAO resultConfigurationDAO1 = ResultConfigurationDAO.list()[0]

        Parameterization parameterization = new Parameterization(parameterizationDAO1.name)
        parameterization.modelClass = CoreModel
        ResultConfiguration configuration = new ResultConfiguration(resultConfigurationDAO1.name)
        configuration.modelClass = CoreModel

        assertFalse renameAction.isUsedInSimulation(parameterization)
        assertFalse renameAction.isUsedInSimulation(configuration)

        SimulationRun run = new SimulationRun(name: "run")
        run.parameterization = parameterizationDAO1
        run.resultConfiguration = resultConfigurationDAO1
        run.model = CoreModel.name
        run.periodCount = 2
        run.iterations = 5
        run.randomSeed = 0
        run.save(flush: true)

        assertTrue renameAction.isUsedInSimulation(parameterization)
        assertTrue renameAction.isUsedInSimulation(configuration)

        Parameterization newparameterization = ModellingItemFactory.incrementVersion(parameterization)
        ResultConfiguration newconfiguration = ModellingItemFactory.incrementVersion(configuration)

        assertTrue renameAction.isUsedInSimulation(newparameterization)
        assertTrue renameAction.isUsedInSimulation(newconfiguration)

    }


}
