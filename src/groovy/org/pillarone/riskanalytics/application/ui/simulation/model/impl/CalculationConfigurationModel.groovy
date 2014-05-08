package org.pillarone.riskanalytics.application.ui.simulation.model.impl

class CalculationConfigurationModel extends SimulationConfigurationModel {

    CalculationConfigurationModel(Class modelClass) {
        super(modelClass)
    }

    @Override
    protected initSubModels(Class modelClass) {
        simulationProfilePaneModel = new CalculationProfilePaneModel(modelClass)
    }

    @Override
    CalculationProfilePaneModel getSimulationProfilePaneModel() {
        super.simulationProfilePaneModel as CalculationProfilePaneModel
    }
}
