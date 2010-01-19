package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SimulationNode extends ItemNode {
    //checkBox selected simulations
    boolean display=true
    // flag for hidden/display simulations
    boolean hidden= false

    public SimulationNode(Simulation simulation) {
        super(simulation, false, true)
    }

    public void setUserObject(Object userObject) {
        if (renameable) {
            item.rename(userObject)
            super.setUserObject("${item.name}")
        }
    }
}