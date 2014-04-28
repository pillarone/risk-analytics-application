package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

class SimulationProfilePaneModel {
    SimulationSettingsPaneModel settingsPaneModel
    SimulationProfileActionsPaneModel simulationProfilePaneActionsModel
    SimulationActionsPaneModel simulationActionsPaneModel

    SimulationProfilePaneModel(Class modelClass, RiskAnalyticsMainModel riskAnalyticsMainModel) {
        initSubModels(modelClass, riskAnalyticsMainModel)
    }

    protected initSubModels(Class modelClass, RiskAnalyticsMainModel riskAnalyticsMainModel) {
        settingsPaneModel = new SimulationSettingsPaneModel(modelClass)
        simulationProfilePaneActionsModel = new SimulationProfileActionsPaneModel(settingsPaneModel, modelClass)
        simulationActionsPaneModel = new SimulationActionsPaneModel(settingsPaneModel)
    }
}

