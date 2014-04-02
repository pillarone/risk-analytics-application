package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel

class SendToSimulationQueueAction extends ResourceBasedAction {

    SimulationSettingsPaneModel simulationSettingsPaneModel

    SendToSimulationQueueAction(String actionName) {
        super(actionName)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        simulationSettingsPaneModel.simul
    }


}
