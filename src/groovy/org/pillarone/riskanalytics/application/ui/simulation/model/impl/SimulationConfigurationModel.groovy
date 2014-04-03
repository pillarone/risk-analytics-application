package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.simulation.model.IBatchListener
import org.pillarone.riskanalytics.application.ui.simulation.model.INewSimulationListener
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SimulationConfigurationModel implements IBatchListener, INewSimulationListener {

    SimulationProfilePaneModel simulationProfilePaneModel

    public SimulationConfigurationModel(Class modelClass, RiskAnalyticsMainModel mainModel) {
        initSubModels(modelClass, mainModel)
    }

    protected initSubModels(Class modelClass, RiskAnalyticsMainModel mainModel) {
        simulationProfilePaneModel = new SimulationProfilePaneModel(modelClass, mainModel)
        mainModel.addNewSimulationListener(this)
        //Use the setting pane model as ISimulationProvider for the actions pane model
    }

    void newBatchAdded(BatchRun batchRun) {
        simulationProfilePaneModel.simulationActionsPaneModel.newBatchAdded(batchRun)
    }

    void newSimulation(Simulation simulation) {
        if (simulation.parameterization) {
            settingsPaneModel.selectedParameterization = simulation.parameterization
        }
        if (simulation.template) {
            settingsPaneModel.selectedResultConfiguration = simulation.template
        }
    }

    SimulationSettingsPaneModel getSettingsPaneModel() {
        simulationProfilePaneModel.settingsPaneModel
    }
}



