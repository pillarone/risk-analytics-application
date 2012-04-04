package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.TabbedPaneManagerHelper
import org.pillarone.riskanalytics.application.ui.result.model.CompareSimulationsViewModel
import org.pillarone.riskanalytics.application.ui.result.view.CompareSimulationsView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareSimulationUIItem extends AbstractUIItem {

    List<Simulation> simulations

    public CompareSimulationUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel, List<Simulation> simulations) {
        super(mainModel, simulationModel)
        this.simulations = simulations
    }

    String createTitle() {
        return TabbedPaneManagerHelper.getTabTitle(Simulation)
    }

    ULCContainer createDetailView() {
        CompareSimulationsView view = new CompareSimulationsView(getViewModel(), mainModel)
        return view.content
    }

    AbstractModellingModel getViewModel() {
        CompareSimulationsViewModel model
        model = new CompareSimulationsViewModel(this.model, ModelStructure.getStructureForModel(this.model.class), simulations)
        return model
    }

    Object getItem() {
        return simulations
    }

    @Override
    boolean isLoaded() {
        return false
    }

    @Override
    boolean isDeletable() {
        return false
    }

    @Override
    void load(boolean completeLoad) {
        simulations.each {Simulation simulation ->
            if (!simulation.isLoaded())
                simulation.load(true)
        }
    }

    @Override
    String getToolTip() {
        return TabbedPaneManagerHelper.getToolTip(simulations)
    }



    @Override
    ULCIcon getIcon() {
        return UIUtils.getIcon("results-active.png")
    }


    void removeAllModellingItemChangeListener() {
        // intentionally blank (probably?)
    }

    void addModellingItemChangeListener(IModellingItemChangeListener listener) {
        // intentionally blank (probably?)
    }

}
