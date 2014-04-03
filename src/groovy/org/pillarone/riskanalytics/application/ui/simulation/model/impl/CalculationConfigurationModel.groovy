package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CalculationConfigurationModel extends SimulationConfigurationModel {

    public CalculationConfigurationModel(Class modelClass, RiskAnalyticsMainModel mainModel) {
        super(modelClass, mainModel)
    }

    @Override
    protected initSubModels(Class modelClass, RiskAnalyticsMainModel mainModel) {
        simulationProfilePaneModel = new CalculationProfilePaneModel(modelClass, mainModel)
        //Use the setting pane model as ISimulationProvider for the actions pane model
    }

    @Override
    CalculationProfilePaneModel getSimulationProfilePaneModel() {
        super.simulationProfilePaneModel as CalculationProfilePaneModel
    }
}
