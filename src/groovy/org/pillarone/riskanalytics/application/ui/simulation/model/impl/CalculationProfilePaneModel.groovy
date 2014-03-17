package org.pillarone.riskanalytics.application.ui.simulation.model.impl

class CalculationProfilePaneModel extends SimulationProfilePaneModel {

    CalculationProfilePaneModel(Class modelClass) {
        super(modelClass)
    }

    @Override
    protected initSubModels(Class modelClass) {
        settingsPaneModel = new CalculationSettingsPaneModel(modelClass)
        simulationProfilePaneActionsModel = new SimulationProfileActionsPaneModel(settingsPaneModel, modelClass)
    }

    @Override
    CalculationSettingsPaneModel getSettingsPaneModel() {
        super.settingsPaneModel as CalculationSettingsPaneModel
    }
}
