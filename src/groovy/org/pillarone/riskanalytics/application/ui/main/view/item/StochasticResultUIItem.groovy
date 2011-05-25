package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.result.view.StochasticResultView
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class StochasticResultUIItem extends ResultUIItem {


    public StochasticResultUIItem(RiskAnalyticsMainModel model, Model simulationModel, Simulation simulation) {
        super(model, simulationModel, simulation)
    }

    ULCContainer createDetailView() {
        ResultViewModel resultViewModel = (ResultViewModel) getViewModel()
        StochasticResultView view = new StochasticResultView(null)
        view.mainModel = mainModel
        view.model = resultViewModel

        resultViewModel.addFunctionListener(view)
        return view.content
    }

    AbstractModellingModel getViewModel() {
        ResultViewModel model = new ResultViewModel(this.model, ModelStructure.getStructureForModel(this.model.class), (Simulation) item)
        mainModel.registerModel(this, model)
        return model
    }




    @Override
    String createTitle() {
        return item.name
    }


}
