package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCFrameOperator
import models.core.CoreModel
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SimulationUIItemTests extends AbstractUIItemTest {

    public void testView() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        assertNotNull frameOperator
        Thread.sleep 5000
    }

    @Override
    AbstractUIItem createUIItem() {
        LocaleResources.setTestMode(true)
        ModellingItemFactory.clear()

        FileImportService.importModelsIfNeeded(["Core"])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['Core'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Core'])
        Parameterization parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('CoreParameters'))
        parameterization.load()

        Model model = new CoreModel()
        model.init()

        Simulation simulation = new Simulation("Simulation")
        simulation.modelClass = CoreModel
        simulation.parameterization = new Parameterization("CoreParameters")
        simulation.template = new ResultConfiguration("CoreResultConfiguration", CoreModel)

        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel()
        SimulationSettingsUIItem uiItem = new SimulationSettingsUIItem(mainModel, model, simulation)
        return uiItem
    }


}
