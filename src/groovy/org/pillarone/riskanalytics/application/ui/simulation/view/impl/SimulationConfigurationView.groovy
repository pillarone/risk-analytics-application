package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCFiller
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationConfigurationModel

/**
 * The SimulationConfigurationView which combines a settings pane (to define a simulation)
 * and an actions pane (to run it).
 */
class SimulationConfigurationView {

    ULCBoxPane content

    SimulationProfilePane simulationProfilePane
    SimulationActionsPane actionsPane
    SimulationConfigurationModel model

    public SimulationConfigurationView(SimulationConfigurationModel model) {
        this.model = model
        initComponents()
        attachListeners(model)
        layoutComponents()
    }

    private void attachListeners(SimulationConfigurationModel model) {
        //to make sure the action pane knows when it's safe to start a simulation
        model.simulationProfilePaneModel.settingsPaneModel.addSimulationValidationListener(actionsPane)
        //used for enabling/disabling the settings pane
        model.actionsPaneModel.addSimulationListener(simulationProfilePane.simulationSettingsPane)
    }

    protected void initComponents() {
        simulationProfilePane = new SimulationProfilePane(model.simulationProfilePaneModel)
        actionsPane = new SimulationActionsPane(model.actionsPaneModel)
    }

    void layoutComponents() {
        ULCBoxPane holder = new ULCBoxPane(1, 2)
        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, simulationProfilePane.content)
        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, actionsPane.content)
        content = new ULCBoxPane(1, 2)
        content.add(ULCBoxPane.BOX_LEFT_TOP, holder)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }
}
