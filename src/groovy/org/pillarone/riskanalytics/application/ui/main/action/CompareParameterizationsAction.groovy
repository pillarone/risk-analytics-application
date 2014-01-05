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
// TODO frahman 2014-01-02 Figure out how to enable compare menu only when more than one param selected (and all from same model ? hopeful..)
class CompareParameterizationsAction extends SelectionTreeAction {

    public CompareParameterizationsAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("CompareParameterizations", tree, model)
    }

// TODO begging to be simplified (probably needs to be fixed)
    public void doActionPerformed(ActionEvent event) {
        List<ParameterizationNode> elements = getSelectedObjects(Parameterization.class)
        validate(elements)
        Model simulationModel = getSelectedModel(elements[0])
        simulationModel.init() // using it immediately before a null check ?!
        if (simulationModel != null && elements[0] != null) {
            List items = elements*.abstractUIItem.item
            CompareParameterizationUIItem uiItem = new CompareParameterizationUIItem(model, simulationModel, items)
            model.openItem(simulationModel, uiItem)
        }
    }

    //fr 2014-01-02 Dont need to declare we throw a java.lang.RuntimeException!
    //Added IllegalStateException to ExceptionSafe so the message appears in the alert
    //(instead of some mysterious runtime problem with a hint to user that logfile has more info)
    //(esp useless when user has no access to logs)
    private void validate(List elements) {
        if (elements.size() < 2){
            throw new IllegalStateException("Pls select two or more parameterizations to compare (and tell developers how you managed to get this error!)")
        }
        Model model = getSelectedModel(elements[0])
        elements.each {
            if (getSelectedModel(it) != model) {
                throw new IllegalStateException("Can only compare parameterizations with same ModelClass")
            }
        }
    }

    // I think this is the 'right' way to do this
    public boolean isEnabled() {

        if(getSelectedObjects(Parameterization.class).size()<2){
            return false;
        }

        return super.isEnabled() //generic checks like user roles
    }


}
