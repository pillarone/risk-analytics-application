package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCFiller
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationConfigurationModel

/**
 * The SimulationConfigurationView which combines a settings pane (to define a simulation)
 * and an actions pane (to run it).
 */
class SimulationConfigurationView implements IDetailView {

    ULCBoxPane content

    SimulationProfilePane simulationProfilePane
    SimulationConfigurationModel model

    SimulationConfigurationView(SimulationConfigurationModel model) {
        this.model = model
        initComponents()
        layoutComponents()
    }

    @Override
    void close() {}

    protected void initComponents() {
        simulationProfilePane = new SimulationProfilePane(model.simulationProfilePaneModel)
    }

    void layoutComponents() {
        ULCBoxPane holder = new ULCBoxPane(1, 3)
        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, simulationProfilePane.content)
        content = new ULCBoxPane(1, 2)
        content.add(ULCBoxPane.BOX_LEFT_TOP, holder)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }
}
