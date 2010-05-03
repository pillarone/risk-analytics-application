package org.pillarone.riskanalytics.application.ui.simulation.action;


import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class CancelSimulationAction extends ResourceBasedAction {

    AbstractConfigurationModel configurationModel;

    public CancelSimulationAction(AbstractConfigurationModel configurationModel) {
        super("Cancel");
        this.configurationModel = configurationModel;
        this.enabled = false
    }

    @Override
    public void doActionPerformed(ActionEvent event) {
        configurationModel.cancelSimulation()
    }
}
