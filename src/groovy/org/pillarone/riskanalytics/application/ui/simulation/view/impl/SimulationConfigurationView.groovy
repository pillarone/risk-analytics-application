package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCBoxPane
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationConfigurationModel
import com.ulcjava.base.application.ULCFiller

/**
 * The SimulationConfigurationView which combines a settings pane (to define a simulation)
 * and an actions pane (to run it).
 */
class SimulationConfigurationView implements ISimulationListener {

    ULCBoxPane content

    private SimulationSettingsPane settingsPane
    private SimulationActionsPane actionsPane

    private SimulationConfigurationModel model

    public SimulationConfigurationView(SimulationConfigurationModel model) {
        this.model = model
        settingsPane = new SimulationSettingsPane(model.settingsPaneModel)
        actionsPane = new SimulationActionsPane(model.actionsPaneModel)
        //to make sure the action pane knows when it's safe to start a simulation
        model.settingsPaneModel.addSimulationValidationListener(actionsPane)
        //used for enabling/disabling the settings pane
        model.actionsPaneModel.addSimulationListener(this)

        layoutComponents()
    }

    void layoutComponents() {
        ULCBoxPane holder = new ULCBoxPane(1, 2)
        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, settingsPane.content)
        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, actionsPane.content)

        content = new ULCBoxPane(1,2)
        content.add(ULCBoxPane.BOX_LEFT_TOP, holder)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }

    void simulationEnd(Simulation simulation, Model model) {
        settingsPane.enable()
        settingsPane.model.simulationName = ""
        settingsPane.simulationName.text = settingsPane.model.simulationName
    }

    void simulationStart(Simulation simulation) {
        settingsPane.disable()
        settingsPane.simulationName.text = simulation.name
    }


}
