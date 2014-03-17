package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import org.pillarone.riskanalytics.application.ui.simulation.model.impl.CalculationConfigurationModel

class CalculationConfigurationView extends SimulationConfigurationView {

    public CalculationConfigurationView(CalculationConfigurationModel model) {
        super(model)
    }

    @Override
    CalculationConfigurationModel getModel() {
        super.model as CalculationConfigurationModel
    }

    @Override
    protected void initComponents() {
        simulationProfilePane = new CalculationProfilePane(model.simulationProfilePaneModel)
        actionsPane = new SimulationActionsPane(model.actionsPaneModel)
    }

    @Override
    CalculationProfilePane getSimulationProfilePane() {
        super.simulationProfilePane as CalculationProfilePane
    }
}
