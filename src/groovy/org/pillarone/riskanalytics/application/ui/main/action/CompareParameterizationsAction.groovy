package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.CompareParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareParameterizationsAction extends SelectionTreeAction {

    public CompareParameterizationsAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("CompareParameterizations", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        List<ParameterizationNode> elements = getSelectedObjects(Parameterization.class)
        validate(elements)
        Model simulationModel = getSelectedModel(elements[0])
        simulationModel.init()
        if (simulationModel != null && elements[0] != null) {
            List items = elements*.abstractUIItem.item
            CompareParameterizationUIItem uiItem = new CompareParameterizationUIItem(model, simulationModel, items)
            model.openItem(simulationModel, uiItem)
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
