package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NewSimulationAction extends SimulationAction {
    Model selectedModel
    ModellingItem selectedItem

    public NewSimulationAction(Model selectedModel, ModellingItem selectedItem, P1RATModel model) {
        super(null, model)
        this.selectedModel = selectedModel
        this.selectedItem = selectedItem
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        openItem(selectedModel, selectedItem)
    }

}
