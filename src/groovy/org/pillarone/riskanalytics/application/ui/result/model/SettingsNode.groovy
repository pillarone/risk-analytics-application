package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tree.DefaultMutableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SettingsNode extends DefaultMutableTreeNode {

    Simulation simulation

    public SettingsNode(Simulation simulation) {
        super('Settings')
        this.simulation = simulation
    }
}
