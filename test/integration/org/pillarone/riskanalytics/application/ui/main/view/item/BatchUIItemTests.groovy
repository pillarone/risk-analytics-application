package org.pillarone.riskanalytics.application.ui.main.view.item

import models.core.CoreModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

/**
 * @author fouad.jaada@intuitive-collaboration.com
 *
 */
class BatchUIItemTests extends AbstractUIItemTest {

    @Override
    AbstractUIItem createUIItem() {
        FileImportService.importModelsIfNeeded(["Core"])
        LocaleResources.testMode = true
        ParameterizationDAO dao = ParameterizationDAO.findByModelClassName(CoreModel.name)
        ResultConfigurationDAO configurationDAO = ResultConfigurationDAO.findByModelClassName(CoreModel.name)

        Parameterization parameterization = new Parameterization(dao.name, CoreModel)
        parameterization.load()
        ResultConfiguration template = new ResultConfiguration(configurationDAO.name, CoreModel)
        template.load()
        assert parameterization.id
        assert template.id

        SimulationProfile simulationProfile = new SimulationProfile('testProfile', CoreModel)
        simulationProfile.template = template
        simulationProfile.numberOfIterations = 5
        simulationProfile.randomSeed = 0
        assert simulationProfile.save()

        Batch batch = new Batch("test")
        batch.parameterizations = [parameterization]
        batch.simulationProfileName = simulationProfile.name
        assert batch.save()
        new BatchUIItem(batch)
    }


    void testView() {
    }
}
