package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class OpenResultsAction extends ResourceBasedAction {

    private SimulationActionsPaneModel model

    public OpenResultsAction(SimulationActionsPaneModel model) {
        super("OpenResults");
        this.model = model
    }

    void doActionPerformed(ActionEvent event) {
        Simulation simulation = model.simulation
        if (!simulation.loaded) {
            simulation.load()
        }
        trace("Open result for simulation: ${simulation.name}")
        model.mainModel.notifyOpenDetailView((Model) simulation.modelClass.newInstance(), simulation)
    }


}
