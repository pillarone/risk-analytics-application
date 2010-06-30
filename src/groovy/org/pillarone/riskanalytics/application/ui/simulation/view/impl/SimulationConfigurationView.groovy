package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCBoxPane
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.model.Model

/**
 * The SimulationConfigurationView which combines a settings pane (to define a simulation)
 * and an actions pane (to run it).
 */
class SimulationConfigurationView implements ISimulationListener {

    ULCBoxPane content

    private SimulationSettingsPane settingsPane
    private SimulationActionsPane actionsPane


    public SimulationConfigurationView(Class modelClass) {
        SimulationSettingsPaneModel settingsModel = new SimulationSettingsPaneModel(modelClass)
        settingsPane = new SimulationSettingsPane(settingsModel)
        //Use the setting pane model as ISimulationProvider for the actions pane model
        SimulationActionsPaneModel actionModel = new SimulationActionsPaneModel(settingsModel)
        actionsPane = new SimulationActionsPane(actionModel)
        //to make sure the action pane knows when it's safe to start a simulation
        settingsModel.addSimulationValidationListener(actionsPane)
        //used for enabling/disabling the settings pane
        actionModel.addSimulationListener(this)

        layoutComponents()
    }

    void layoutComponents() {
        content = new ULCBoxPane(1, 2)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, settingsPane.content)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, actionsPane.content)
    }

    void simulationEnd(Simulation simulation, Model model) {
        settingsPane.enable()
    }

    void simulationStart(Simulation simulation) {
        settingsPane.disable()
    }


}
