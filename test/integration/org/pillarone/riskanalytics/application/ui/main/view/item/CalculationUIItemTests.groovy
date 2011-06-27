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
import org.pillarone.riskanalytics.core.output.DBCleanUpService
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
        new DBCleanUpService().cleanUp()
        LocaleResources.setTestMode()
        ModellingItemFactory.clear()

        FileImportService.importModelsIfNeeded(["DeterministicApplication"])
        Parameterization parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('DeterministicApplicationParameters'))
        parameterization.load()

        Model model = new DeterministicApplicationModel()
        model.init()

        Simulation simulation = new Simulation("Simulation")
        simulation.modelClass = CoreModel
        simulation.parameterization = new Parameterization("DeterministicApplicationParameters")
        simulation.template = new ResultConfiguration("DeterministicApplicationResultConfiguration")

        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel()
        CalculationUIItem uiItem = new CalculationUIItem(mainModel, model, simulation)
        return uiItem
    }


}