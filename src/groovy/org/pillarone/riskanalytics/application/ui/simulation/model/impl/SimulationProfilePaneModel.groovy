package org.pillarone.riskanalytics.application.ui.simulation.model.impl


class SimulationProfilePaneModel {
    SimulationSettingsPaneModel settingsPaneModel
    SimulationProfileActionsPaneModel simulationProfilePaneActionsModel

    SimulationProfilePaneModel(Class modelClass) {
        initSubModels(modelClass)
    }

    protected initSubModels(Class modelClass) {
        settingsPaneModel = new SimulationSettingsPaneModel(modelClass)
        simulationProfilePaneActionsModel = new SimulationProfileActionsPaneModel(settingsPaneModel, modelClass)
    }
}

