package org.pillarone.riskanalytics.application.ui.main.view
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.main.action.SimulationAction
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SimulationRunMenuItem extends ULCMenuItem implements ITreeSelectionListener {

    SimulationAction simulationAction

    public SimulationRunMenuItem(SimulationAction simulationAction) {
        super(simulationAction)
        this.simulationAction = simulationAction
    }

    void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        boolean enabled = simulationAction.selectedModel
        setEnabled enabled
        simulationAction.enabled = enabled
    }


}
