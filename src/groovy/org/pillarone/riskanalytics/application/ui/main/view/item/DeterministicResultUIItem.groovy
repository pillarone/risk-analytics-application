package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.util.ULCIcon
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.result.model.AbstractResultViewModel
import org.pillarone.riskanalytics.application.ui.result.model.DeterministicResultViewModel
import org.pillarone.riskanalytics.application.ui.result.view.DeterministicResultView
import org.pillarone.riskanalytics.application.ui.result.view.ResultView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DeterministicResultUIItem extends SimulationResultUIItem {

    DeterministicResultUIItem(DeterministicModel simulationModel, Simulation simulation) {
        super(simulationModel, simulation)
    }

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel', NavigationTableTreeModel)
    }

    @Override
    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    @Override
    protected ResultView createView(AbstractResultViewModel model) {
        return new DeterministicResultView(model, riskAnalyticsMainModel)
    }

    AbstractResultViewModel getViewModel() {
        AbstractResultViewModel model = new DeterministicResultViewModel(this.model, ModelStructure.getStructureForModel(this.model.class), (Simulation) item)
        riskAnalyticsMainModel.registerModel(this, model)
        return model
    }

    @Override
    String createTitle() {
        return item.name
    }

    @Override
    ULCIcon getIcon() {
        return UIUtils.getIcon("results-active.png")
    }

}
