package org.pillarone.riskanalytics.application.ui.simulation.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel


public class StopSimulationAction extends ResourceBasedAction {

    AbstractConfigurationModel configurationModel

    public StopSimulationAction(AbstractConfigurationModel simConfigModel) {
        super("Stop")
        this.configurationModel = simConfigModel
        enabled = false
    }

    public void doActionPerformed(ActionEvent event) {
        configurationModel.stopSimulation()
    }


}