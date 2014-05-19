package org.pillarone.riskanalytics.application.ui.main.view.item
import grails.util.Holders
import org.pillarone.riskanalytics.application.dataaccess.function.MeanFunction
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.result.view.StochasticResultView
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class StochasticResultUIItem extends SimulationResultUIItem<StochasticResultView> {


    StochasticResultUIItem(Simulation simulation) {
        super(simulation)
    }

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel', NavigationTableTreeModel)
    }

    StochasticResultView createDetailView() {
        ResultViewModel model = viewModel
        StochasticResultView view = new StochasticResultView(model)
        model.addFunctionListener(view)
        model.addFunction(new MeanFunction())
        return view
    }

    private ResultViewModel getViewModel() {
        return new ResultViewModel(this.model, ModelStructure.getStructureForModel(this.model.class), (Simulation) item)
    }

    @Override
    String createTitle() {
        return item.name
    }


}
