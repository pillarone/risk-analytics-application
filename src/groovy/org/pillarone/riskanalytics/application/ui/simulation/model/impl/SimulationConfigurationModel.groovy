package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.BatchRun

class SimulationConfigurationModel implements BatchListener {

    SimulationSettingsPaneModel settingsPaneModel
    SimulationActionsPaneModel actionsPaneModel


    public SimulationConfigurationModel(Class modelClass, P1RATModel mainModel) {
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