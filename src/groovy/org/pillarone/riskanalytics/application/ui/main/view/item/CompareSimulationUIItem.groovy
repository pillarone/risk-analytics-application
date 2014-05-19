package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.main.view.TabbedPaneManagerHelper
import org.pillarone.riskanalytics.application.ui.result.model.CompareSimulationsViewModel
import org.pillarone.riskanalytics.application.ui.result.view.CompareSimulationsView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareSimulationUIItem extends AbstractUIItem<CompareSimulationsView> {

    List<Simulation> simulations
    final Model model

    CompareSimulationUIItem(Model model, List<Simulation> simulations) {
        this.model = model
        this.simulations = simulations
    }

    String createTitle() {
        return TabbedPaneManagerHelper.getTabTitle(Simulation)
    }

    CompareSimulationsView createDetailView() {
        return new CompareSimulationsView(viewModel)
    }

    private CompareSimulationsViewModel getViewModel() {
        new CompareSimulationsViewModel(model, ModelStructure.getStructureForModel(model.class), simulations)
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
        simulations.each { Simulation simulation ->
            if (!simulation.loaded)
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

    String getWindowTitle() {
        String windowTitle = model ? model.name : ""
        windowTitle += " " + createTitle()
        return windowTitle
    }
}
