package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.simulation.model.INewSimulationListener
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SimulationConfigurationModel implements INewSimulationListener {

    SimulationProfilePaneModel simulationProfilePaneModel
    final RiskAnalyticsMainModel mainModel

    SimulationConfigurationModel(Class modelClass, RiskAnalyticsMainModel mainModel) {
        this.mainModel = mainModel
        initSubModels(modelClass)
        attachListener()
    }

    protected void attachListener() {
        mainModel.addModellingItemEventListener(simulationProfilePaneModel.settingsPaneModel)
        mainModel.addNewSimulationListener(this)
    }

    void close() {
        mainModel.removeModellingItemEventListener(simulationProfilePaneModel.settingsPaneModel)
        mainModel.removeNewSimulationListener(this)
    }

    protected initSubModels(Class modelClass) {
        simulationProfilePaneModel = new SimulationProfilePaneModel(modelClass, mainModel)
    }

    void newSimulation(Simulation simulation) {
        if (simulation.modelClass == simulationProfilePaneModel.settingsPaneModel.modelClass) {
            if (simulation.parameterization) {
                settingsPaneModel.selectedParameterization = simulation.parameterization
            }
            if (simulation.template) {
                settingsPaneModel.selectedResultConfiguration = simulation.template
            }
        }
    }

    SimulationSettingsPaneModel getSettingsPaneModel() {
        simulationProfilePaneModel.settingsPaneModel
    }
}



