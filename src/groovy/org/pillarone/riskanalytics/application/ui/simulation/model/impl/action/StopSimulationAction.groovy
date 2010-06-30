package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import com.ulcjava.base.application.event.ActionEvent


class StopSimulationAction extends ResourceBasedAction {

    private SimulationActionsPaneModel model

    public StopSimulationAction(SimulationActionsPaneModel model) {
        super("Stop");
        this.model = model;
    }

    void doActionPerformed(ActionEvent event) {
        model.stopSimulation()
    }


}
