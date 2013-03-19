package org.pillarone.riskanalytics.application.ui.main.view.item

import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.result.view.StochasticResultView
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.ui.result.view.ResultView
import org.pillarone.riskanalytics.application.ui.result.model.AbstractResultViewModel
import org.pillarone.riskanalytics.application.dataaccess.function.MeanFunction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class StochasticResultUIItem extends ResultUIItem {


    public StochasticResultUIItem(RiskAnalyticsMainModel model, Model simulationModel, Simulation simulation) {
        super(model, simulationModel, simulation)
    }

    @Override
    protected ResultView createView(AbstractResultViewModel model) {
        model = model as ResultViewModel

        StochasticResultView view = new StochasticResultView(model, mainModel)
        model.addFunctionListener(view)
        model.addFunction(new MeanFunction())
        return view
    }

    AbstractModellingModel getViewModel() {
        ResultViewModel model = new ResultViewModel(this.model, ModelStructure.getStructureForModel(this.model.class), (Simulation) item)
        mainModel.registerModel(this, model)
        return model
    }

    @Override
    public boolean remove() {
        boolean removed = super.remove()
        if (removed){
            Parameterization parameterization = ((Simulation) item).parameterization
            //after deleting a simulation, delete a lock tag if the p14n is not used
            parameterization.addRemoveLockTag()
            navigationTableTreeModel.itemChanged(parameterization)
        }

        return removed
    }



    @Override
    String createTitle() {
        return item.name
    }


}
