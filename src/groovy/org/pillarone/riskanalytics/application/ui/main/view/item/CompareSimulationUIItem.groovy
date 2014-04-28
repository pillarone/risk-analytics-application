package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.util.ULCIcon
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
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
class CompareSimulationUIItem extends AbstractUIItem {

    List<Simulation> simulations
    private final Model model

    CompareSimulationUIItem(Model model, List<Simulation> simulations) {
        this.model = model
        this.simulations = simulations
    }

    void close() {
        riskAnalyticsMainModel.closeItem(model, this)
    }

    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    String createTitle() {
        return TabbedPaneManagerHelper.getTabTitle(Simulation)
    }

    IDetailView createDetailView() {
        return new CompareSimulationsView(viewModel as CompareSimulationsViewModel, riskAnalyticsMainModel)
    }

    AbstractModellingModel getViewModel() {
        CompareSimulationsViewModel model
        model = new CompareSimulationsViewModel(this.model, ModelStructure.getStructureForModel(this.model.class), simulations)
        return model
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
