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
class CompareParameterizationsAction extends AbstractCompareAction {

    public CompareParameterizationsAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("CompareParameterizations", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        List<ParameterizationNode> elements = getSelectedObjects(Parameterization.class)
        try {
            validate()
            Model simulationModel = getSelectedModel(elements[0])
            simulationModel.init()
            if (elements[0] != null) {
                List items = elements*.abstractUIItem.item
                CompareParameterizationUIItem uiItem = new CompareParameterizationUIItem(model, simulationModel, items)
                model.openItem(simulationModel, uiItem)
            }
        } catch (IllegalArgumentException ex) {
            println "$ex"
        }
    }

}
