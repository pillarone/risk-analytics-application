package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.util.ULCIcon
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.result.model.DeterministicResultViewModel
import org.pillarone.riskanalytics.application.ui.result.view.DeterministicResultView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DeterministicResultUIItem extends SimulationResultUIItem {

    DeterministicResultUIItem(Simulation simulation) {
        super(simulation)
    }

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel', NavigationTableTreeModel)
    }

    IDetailView createDetailView() {
        new DeterministicResultView(viewModel)
    }


    private DeterministicResultViewModel getViewModel() {
        return new DeterministicResultViewModel(this.model, ModelStructure.getStructureForModel(this.model.class), (Simulation) item)
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
