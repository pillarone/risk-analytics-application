package org.pillarone.riskanalytics.application.ui.simulation.model.impl

class SimulationProfilePaneModel {
    SimulationSettingsPaneModel settingsPaneModel
    SimulationProfileActionsPaneModel simulationProfilePaneActionsModel
    SimulationActionsPaneModel simulationActionsPaneModel

    SimulationProfilePaneModel(Class modelClass) {
        initSubModels(modelClass)
    }

    protected initSubModels(Class modelClass) {
        settingsPaneModel = new SimulationSettingsPaneModel(modelClass)
        simulationProfilePaneActionsModel = new SimulationProfileActionsPaneModel(settingsPaneModel, modelClass)
        simulationActionsPaneModel = new SimulationActionsPaneModel(settingsPaneModel)
    }
}

