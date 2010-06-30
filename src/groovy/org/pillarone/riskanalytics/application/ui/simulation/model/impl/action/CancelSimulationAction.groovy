package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction


class CancelSimulationAction extends ResourceBasedAction {

    private SimulationActionsPaneModel model

    public CancelSimulationAction(SimulationActionsPaneModel model) {
        super("Cancel");
        this.model = model;
    }

    void doActionPerformed(ActionEvent event) {
        model.cancelSimulation()
    }
}
