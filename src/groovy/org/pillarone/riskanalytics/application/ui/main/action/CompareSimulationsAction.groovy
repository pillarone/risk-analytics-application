package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareSimulationsAction extends SelectionTreeAction {

    public CompareSimulationsAction(ULCTableTree tree, P1RATModel model) {
        super("CompareSimulations", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        List elements = getSelectedObjects(Simulation.class)
        try {
            validate(elements)
            Model model = getSelectedModel(elements[0])
            model.init()
            if (model != null && elements[0].item != null) {
                this.model.compareItems(model, elements)
            }
        } catch (IllegalArgumentException ex) {
            println "$ex"
        }
    }

    private void validate(List elements) throws IllegalArgumentException, Exception {
        if (elements.size() < 2) throw new IllegalArgumentException("select at lease two simulations for compare")
        Class modelClass = elements[0].item.modelClass
        elements.each {
            if (it.item.modelClass != modelClass) {
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
