package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import com.ulcjava.base.application.event.ActionEvent

import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


class OpenResultsAction extends ResourceBasedAction {

    private static Log LOG = LogFactory.getLog(OpenResultsAction)

    private SimulationActionsPaneModel model

    public OpenResultsAction(SimulationActionsPaneModel model) {
        super("OpenResults");
        this.model = model
    }

    void doActionPerformed(ActionEvent event) {
        Simulation simulation = model.simulation
        simulation.load()
        LOG.trace "Reading end time from simulation: ${System.identityHashCode(simulation)}: ${simulation.end?.time}"
        model.mainModel.openItem(simulation.modelClass.newInstance(), simulation)
    }


}
