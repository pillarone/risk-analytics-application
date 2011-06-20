package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.BatchRun

class SimulationConfigurationModel implements BatchListener {

    SimulationSettingsPaneModel settingsPaneModel
    SimulationActionsPaneModel actionsPaneModel


    public SimulationConfigurationModel(Class modelClass, RiskAnalyticsMainModel mainModel) {
        settingsPaneModel = new SimulationSettingsPaneModel(modelClass)
        //Use the setting pane model as ISimulationProvider for the actions pane model
        actionsPaneModel = new SimulationActionsPaneModel(settingsPaneModel, mainModel)
    }

    void newBatchAdded(BatchRun batchRun) {
        actionsPaneModel.newBatchAdded(batchRun)
    }


}

interface BatchListener {

    void newBatchAdded(BatchRun batchRun)
}