package org.pillarone.riskanalytics.application.ui.main.view.item

import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.CalculationConfigurationModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.CalculationConfigurationView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class CalculationSettingsUIItem extends SimulationSettingsUIItem {

    CalculationSettingsUIItem(Model simulationModel, Simulation simulation) {
        super(simulationModel, simulation)
    }

    String createTitle() {
        return UIUtils.getText(SimulationSettingsUIItem.class, "calculation")
    }

    IDetailView createDetailView() {
        return new CalculationConfigurationView(viewModel)
    }

    CalculationConfigurationModel getViewModel() {
        CalculationConfigurationModel model = new CalculationConfigurationModel(model.class, riskAnalyticsMainModel)
        model.settingsPaneModel.selectedParameterization = item.parameterization
        model.settingsPaneModel.selectedResultConfiguration = item.template
        riskAnalyticsMainModel.registerModel(this, model)
        return model
    }
}
