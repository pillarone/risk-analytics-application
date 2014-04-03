package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

class CalculationProfilePaneModel extends SimulationProfilePaneModel {

    CalculationProfilePaneModel(Class modelClass,RiskAnalyticsMainModel riskAnalyticsMainModel) {
        super(modelClass,riskAnalyticsMainModel)
    }

    @Override
    protected initSubModels(Class modelClass,RiskAnalyticsMainModel riskAnalyticsMainModel) {
        settingsPaneModel = new CalculationSettingsPaneModel(modelClass)
        simulationProfilePaneActionsModel = new SimulationProfileActionsPaneModel(settingsPaneModel, modelClass)
        simulationActionsPaneModel = new SimulationActionsPaneModel(settingsPaneModel, riskAnalyticsMainModel)
    }

    @Override
    CalculationSettingsPaneModel getSettingsPaneModel() {
        super.settingsPaneModel as CalculationSettingsPaneModel
    }
}
