package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SimulationAction extends SelectionTreeAction {

    public SimulationAction(ULCTree tree, P1RATModel model) {
        super("RunSimulation", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        Model selectedModel = getSelectedModel()
        Object selectedItem = getSelectedItem()
        Simulation simulation = new Simulation("Simulation")
        simulation.parameterization = selectedItem instanceof Parameterization ? selectedItem : null
        simulation.template = selectedItem instanceof ResultConfiguration ? selectedItem : null
        model.openItem(selectedModel, simulation)
    }

}