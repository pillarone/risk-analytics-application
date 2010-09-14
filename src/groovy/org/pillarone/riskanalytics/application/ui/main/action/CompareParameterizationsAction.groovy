package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareParameterizationsAction extends SelectionTreeAction {

    public CompareParameterizationsAction(ULCTree tree, P1RATModel model) {
        super("CompareParameterizations", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        List elements = getSelectedObjects(Parameterization.class)
        try {
            validate(elements)
            Model model = getSelectedModel(elements[0])
            model.init()
            if (model != null && elements[0] != null) {
                this.model.compareParameterizations(model, elements)
            }
        } catch (IllegalArgumentException ex) {
            println "$ex"
        }
    }

    private void validate(List elements) throws IllegalArgumentException {
        if (elements.size() < 2) throw new IllegalArgumentException("select at lease two parameterizations for compare")
        Model model = getSelectedModel(elements[0])
        elements.each {
            if (getSelectedModel(it) != model) {
                throw new IllegalArgumentException("select a parameterizations with same ModelClass")
            }
        }
    }

    public boolean isEnabled() {
        List elements = getSelectedObjects(Parameterization.class)
        try {
            validate(elements)
        } catch (IllegalArgumentException ex) {
            return false
        }
        return true
    }

}
