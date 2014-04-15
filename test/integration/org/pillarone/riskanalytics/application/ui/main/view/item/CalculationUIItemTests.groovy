package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCFrameOperator
import models.core.CoreModel
import models.deterministicApplication.DeterministicApplicationModel
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CalculationUIItemTests extends AbstractUIItemTest {

    public void testView() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        assertNotNull frameOperator
    }


    @Override
    AbstractUIItem createUIItem() {
        LocaleResources.setTestMode(true)
        ModellingItemFactory.clear()

        FileImportService.importModelsIfNeeded(["DeterministicApplication"])
        Parameterization parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('DeterministicApplicationParameters'))
        parameterization.load()

        Model model = new DeterministicApplicationModel()
        model.init()

        Simulation simulation = new Simulation("Simulation")
        simulation.parameterization = new Parameterization("DeterministicApplicationParameters")
        simulation.template = new ResultConfiguration("DeterministicApplicationResultConfiguration", CoreModel)

        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel()
        CalculationSettingsUIItem uiItem = new CalculationSettingsUIItem(mainModel, model, simulation)
        return uiItem
    }


}
