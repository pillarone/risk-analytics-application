package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode

class SimulationNode extends ItemNode {
    //checkBox selected simulations
    boolean display = true
    // flag for hidden/display simulations
    boolean hidden = false

    public SimulationNode(Simulation simulation) {
        super(simulation, false, true)
    }

    public void setUserObject(Object userObject) {
        if (renameable) {
            item.rename(userObject)
            setValueAt("${item.name}".toString(), 0)
        }
    }
}