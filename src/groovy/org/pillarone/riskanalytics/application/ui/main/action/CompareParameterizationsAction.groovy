package org.pillarone.riskanalytics.application.ui.main.action

import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareParameterizationsAction extends AbstractCompareAction {

    public CompareParameterizationsAction(ULCTableTree tree, P1RATModel model) {
        super("CompareParameterizations", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        List elements = getSelectedObjects(Parameterization.class)
        try {
            validate()
            Model model = getSelectedModel(elements[0])
            model.init()
            if (model != null && elements[0] != null) {
                this.model.compareParameterizations(model, elements*.item)
            }
        } catch (IllegalArgumentException ex) {
            println "$ex"
        }
    }

}
