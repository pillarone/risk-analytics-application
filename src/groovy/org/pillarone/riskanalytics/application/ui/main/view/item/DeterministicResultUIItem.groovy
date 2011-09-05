package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.result.model.DeterministicResultViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.result.view.DeterministicResultView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DeterministicResultUIItem extends ResultUIItem {

    public DeterministicResultUIItem(RiskAnalyticsMainModel model, DeterministicModel simulationModel, Simulation simulation) {
        super(model, simulationModel, simulation)
    }

    ULCContainer createDetailView() {
        ResultViewModel resultViewModel = getViewModel()
        DeterministicResultView view = new DeterministicResultView(null)
        view.mainModel = mainModel
        view.model = resultViewModel
        resultViewModel.addFunctionListener(view)
        return view.content
    }

    ResultViewModel getViewModel() {
        ResultViewModel model = new DeterministicResultViewModel(this.model, ModelStructure.getStructureForModel(this.model.class), (Simulation) item)
        mainModel.registerModel(this, model)
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
