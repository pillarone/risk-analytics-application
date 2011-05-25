package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.main.model.IContentModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.BatchRun

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CalculationConfigurationModel implements BatchListener, IContentModel {

    SimulationSettingsPaneModel settingsPaneModel
    SimulationActionsPaneModel actionsPaneModel

    public CalculationConfigurationModel(Class modelClass, RiskAnalyticsMainModel mainModel) {
        settingsPaneModel = new CalculationSettingsPaneModel(modelClass)
        //Use the setting pane model as ISimulationProvider for the actions pane model
        actionsPaneModel = new SimulationActionsPaneModel(settingsPaneModel, mainModel)
    }

    void newBatchAdded(BatchRun batchRun) {
        actionsPaneModel.newBatchAdded(batchRun)
    }
}
