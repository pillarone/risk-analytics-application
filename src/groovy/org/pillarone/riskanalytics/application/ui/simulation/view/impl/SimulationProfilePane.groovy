package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCFiller
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationProfilePaneModel

class SimulationProfilePane {

    SimulationProfilePaneModel model
    SimulationSettingsPane simulationSettingsPane
    SimulationActionsPane newSimulationActionPane

    private SimulationProfileActionsPane simulationProfileActionsPane
    protected ULCBoxPane content

    SimulationProfilePane(SimulationProfilePaneModel simulationProfilePaneModel) {
        this.model = simulationProfilePaneModel
        initComponents()
        layout()
        attachListeners()
    }

    protected initComponents() {
        simulationProfileActionsPane = new SimulationProfileActionsPane(model.simulationProfilePaneActionsModel)
        simulationSettingsPane = new SimulationSettingsPane(model.settingsPaneModel)
        newSimulationActionPane = new SimulationActionsPane(model.simulationActionsPaneModel)
    }

    protected attachListeners() {
        model.settingsPaneModel.addSimulationValidationListener(newSimulationActionPane)
    }

    protected void layout() {
        content = new ULCBoxPane(1, 0)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, simulationProfileActionsPane.content)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, newSimulationActionPane.content)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, simulationSettingsPane.content)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, ULCFiller.createGlue())
    }

    ULCComponent getContent() {
        content
    }
}
