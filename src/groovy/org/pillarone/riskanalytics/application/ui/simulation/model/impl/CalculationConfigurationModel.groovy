package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

class CalculationConfigurationModel extends SimulationConfigurationModel {

    CalculationConfigurationModel(Class modelClass, RiskAnalyticsMainModel mainModel) {
        super(modelClass, mainModel)
    }

    @Override
    protected initSubModels(Class modelClass) {
        simulationProfilePaneModel = new CalculationProfilePaneModel(modelClass, mainModel)
    }

    @Override
    CalculationProfilePaneModel getSimulationProfilePaneModel() {
        super.simulationProfilePaneModel as CalculationProfilePaneModel
    }
}
