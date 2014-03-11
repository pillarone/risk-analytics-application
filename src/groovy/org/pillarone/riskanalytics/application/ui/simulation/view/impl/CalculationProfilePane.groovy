package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCFiller
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.CalculationProfilePaneModel

public class CalculationProfilePane extends SimulationProfilePane {

    CalculationProfilePane(CalculationProfilePaneModel simulationProfilePaneModel) {
        super(simulationProfilePaneModel)
    }

    @Override
    protected initComponents() {
        simulationSettingsPane = new CalculationSettingsPane(model.settingsPaneModel)
    }

    @Override
    protected void layout() {
        content = new ULCBoxPane(1, 2)
        ULCBoxPane holder = new ULCBoxPane(1, 2)
        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, simulationSettingsPane.content)
        content.add(ULCBoxPane.BOX_LEFT_TOP, holder)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }

    @Override
    CalculationProfilePaneModel getModel() {
        super.model as CalculationProfilePaneModel
    }

    @Override
    CalculationSettingsPane getSimulationSettingsPane() {
        super.simulationSettingsPane as CalculationSettingsPane
    }
}