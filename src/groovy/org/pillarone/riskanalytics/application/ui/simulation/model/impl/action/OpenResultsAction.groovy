package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class OpenResultsAction extends ResourceBasedAction {

    private static Log LOG = LogFactory.getLog(OpenResultsAction)

    private SimulationActionsPaneModel model

    public OpenResultsAction(SimulationActionsPaneModel model) {
        super("OpenResults");
        this.model = model
    }

    void doActionPerformed(ActionEvent event) {
        Simulation simulation = model.simulation
        if (!simulation.isLoaded()) {
            simulation.load()
        }
        trace("Open result for simulation: ${simulation.name}")
        model.mainModel.notifyOpenDetailView((Model) simulation.modelClass.newInstance(), simulation)
    }


}
