package org.pillarone.riskanalytics.application.ui.main.view.item
import grails.util.Holders
import org.pillarone.riskanalytics.application.dataaccess.function.MeanFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.result.model.AbstractResultViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.result.view.ResultView
import org.pillarone.riskanalytics.application.ui.result.view.StochasticResultView
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class StochasticResultUIItem extends SimulationResultUIItem {


    StochasticResultUIItem(Model simulationModel, Simulation simulation) {
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
        model = model as ResultViewModel

        StochasticResultView view = new StochasticResultView(model, riskAnalyticsMainModel)
        model.addFunctionListener(view)
        model.addFunction(new MeanFunction())
        return view
    }

    AbstractModellingModel getViewModel() {
        ResultViewModel model = new ResultViewModel(this.model, ModelStructure.getStructureForModel(this.model.class), (Simulation) item)
        riskAnalyticsMainModel.registerModel(this, model)
        return model
    }

    @Override
    String createTitle() {
        return item.name
    }


}
