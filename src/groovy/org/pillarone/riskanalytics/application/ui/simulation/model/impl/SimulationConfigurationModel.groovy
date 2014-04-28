package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.simulation.model.INewSimulationListener
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SimulationConfigurationModel implements INewSimulationListener {

    SimulationProfilePaneModel simulationProfilePaneModel

    public SimulationConfigurationModel(Class modelClass, RiskAnalyticsMainModel mainModel) {
        initSubModels(modelClass, mainModel)
    }

    protected initSubModels(Class modelClass, RiskAnalyticsMainModel mainModel) {
        simulationProfilePaneModel = new SimulationProfilePaneModel(modelClass, mainModel)
        //Use the setting pane model as ISimulationProvider for the actions pane model
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



