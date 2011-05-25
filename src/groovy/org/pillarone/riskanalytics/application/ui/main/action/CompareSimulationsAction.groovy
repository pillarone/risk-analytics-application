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
class CompareSimulationsAction extends SelectionTreeAction {

    public CompareSimulationsAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("CompareSimulations", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        List elements = getSelectedObjects(Simulation.class)
        try {
            validate(elements)
            Model selectedModel = getSelectedModel(elements[0])
            selectedModel.init()
            if (selectedModel != null) {
                List items = elements*.abstractUIItem.item
                CompareSimulationUIItem uiItem = new CompareSimulationUIItem(model, selectedModel, items)
                model.openItem(selectedModel, uiItem)
            }
        } catch (IllegalArgumentException ex) {
            println "$ex"
        }
    }

    private void validate(List elements) throws IllegalArgumentException, Exception {
        if (elements.size() < 2) throw new IllegalArgumentException("select at lease two simulations for compare")
        Class modelClass = elements[0].abstractUIItem.item.modelClass
        elements.each {
            if (it.abstractUIItem.item.modelClass != modelClass) {
                throw new IllegalArgumentException("select a simulations with same ModelClass")
            }
        }
    }

    public boolean isEnabled() {
        List elements = getSelectedObjects(Simulation.class)
        try {
            validate(elements)
        } catch (IllegalArgumentException ex) {
            return false
        }
        Model selectedModel = getSelectedModel(elements[0])
        //at moment compare of deterministic models not supported
        return !(selectedModel instanceof DeterministicModel)
    }

}
