package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.application.ui.simulation.model.INewSimulationListener
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SimulationConfigurationModel implements BatchListener, INewSimulationListener {

    SimulationSettingsPaneModel settingsPaneModel
    SimulationActionsPaneModel actionsPaneModel


    public SimulationConfigurationModel(Class modelClass, RiskAnalyticsMainModel mainModel) {
        settingsPaneModel = new SimulationSettingsPaneModel(modelClass)
        //Use the setting pane model as ISimulationProvider for the actions pane model
        actionsPaneModel = new SimulationActionsPaneModel(settingsPaneModel, mainModel)
        mainModel.addNewSimulationListener(this)
    }

    void newBatchAdded(BatchRun batchRun) {
        actionsPaneModel.newBatchAdded(batchRun)
    }

    void newSimulation(Simulation simulation) {
        if (simulation.parameterization)
            settingsPaneModel.selectedParameterization = simulation.parameterization
        if (simulation.template)
            settingsPaneModel.selectedResultConfiguration = simulation.template
    }


}

interface BatchListener {

    void newBatchAdded(BatchRun batchRun)
}