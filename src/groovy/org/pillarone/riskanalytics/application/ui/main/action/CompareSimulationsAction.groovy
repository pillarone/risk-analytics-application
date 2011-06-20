package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.CompareSimulationUIItem
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareSimulationsAction extends AbstractCompareAction {

    public CompareSimulationsAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("CompareSimulations", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        List elements = getSelectedObjects(Simulation.class)
        try {
            validate()
            Model selectedModel = getSelectedModel(elements[0])
            selectedModel.init()
            if (elements[0].item != null) {
                List items = elements*.abstractUIItem.item
                CompareSimulationUIItem uiItem = new CompareSimulationUIItem(model, selectedModel, items)
                model.openItem(selectedModel, uiItem)
            }
        } catch (IllegalArgumentException ex) {
            println "$ex"
        }
    }

    public boolean isEnabled() {
        return super.isEnabled() && !(getSelectedModel() instanceof DeterministicModel)
    }

}
