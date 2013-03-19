package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import org.pillarone.riskanalytics.application.ui.simulation.model.impl.CalculationConfigurationModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CalculationConfigurationView extends SimulationConfigurationView {

    public CalculationConfigurationView(CalculationConfigurationModel model) {
        settingsPane = new CalculationSettingsPane(model.settingsPaneModel)
        actionsPane = new SimulationActionsPane(model.actionsPaneModel)
        //to make sure the action pane knows when it's safe to start a simulation
        model.settingsPaneModel.addSimulationValidationListener(actionsPane)
        //used for enabling/disabling the settings pane
        model.actionsPaneModel.addSimulationListener(this)
        layoutComponents()
    }


}
