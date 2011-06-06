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
class CompareSimulationsAction extends AbstractCompareAction {

    public CompareSimulationsAction(ULCTableTree tree, P1RATModel model) {
        super("CompareSimulations", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        List elements = getSelectedObjects(Simulation.class)
        try {
            validate()
            Model model = getSelectedModel(elements[0])
            model.init()
            if (model != null && elements[0].item != null) {
                this.model.compareItems(model, elements)
            }
        } catch (IllegalArgumentException ex) {
            println "$ex"
        }
    }

    public boolean isEnabled() {
        return super.isEnabled() && !(getSelectedModel() instanceof DeterministicModel)
    }

}
