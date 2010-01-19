package org.pillarone.riskanalytics.application.ui.simulation.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class OpenResultAction extends ResourceBasedAction {

    AbstractConfigurationModel configurationModel

    public OpenResultAction(AbstractConfigurationModel simConfigModel) {
        super("OpenResults")
        this.configurationModel = simConfigModel
        enabled = false
    }


    public void doActionPerformed(ActionEvent event) {
        Simulation simulation = configurationModel.getCurrentSimulation()
        Model modelInstance = configurationModel.getSelectedModel().newInstance()
        configurationModel.mainModel.openItem(modelInstance, simulation)
    }

}

