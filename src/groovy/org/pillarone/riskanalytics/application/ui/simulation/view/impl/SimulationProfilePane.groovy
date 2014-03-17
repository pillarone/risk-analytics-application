package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCFiller
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationProfilePaneModel

class SimulationProfilePane {

    SimulationProfilePaneModel model
    SimulationSettingsPane simulationSettingsPane
    private SimulationProfileActionsPane simulationProfileActionsPane
    protected ULCBoxPane content

    SimulationProfilePane(SimulationProfilePaneModel simulationProfilePaneModel) {
        this.model = simulationProfilePaneModel
        initComponents()
        layout()
    }

    protected initComponents() {
        simulationProfileActionsPane = new SimulationProfileActionsPane(model.simulationProfilePaneActionsModel)
        simulationSettingsPane = new SimulationSettingsPane(model.settingsPaneModel)
    }

    protected void layout() {
        content = new ULCBoxPane(1, 2)
        ULCBoxPane holder = new ULCBoxPane(1, 2)
        holder.border = BorderFactory.createTitledBorder("Simulation Profiles")
        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, simulationProfileActionsPane.content)
        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, simulationSettingsPane.content)
        content.add(ULCBoxPane.BOX_LEFT_TOP, holder)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }

    ULCComponent getContent() {
        content
    }
}
