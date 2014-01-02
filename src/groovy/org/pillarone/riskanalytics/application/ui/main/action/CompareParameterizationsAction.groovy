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
    //Added IllegalStateException to list in ExceptionSafe so the message appears in the alert
    //(instead of some mysterious runtime problem with a hint to user that logfile has more info)
    //esp useless when user has no access to logs...
    private void validate(List elements) {
        if (elements.size() < 2) throw new IllegalStateException("select two or more parameterizations for comparing")
        Model model = getSelectedModel(elements[0])
        elements.each {
            if (getSelectedModel(it) != model) {
                throw new IllegalStateException("select a parameterizations with same ModelClass")
            }
        }
    }

    //frahman 2014-01-02 Adding logging reveals this exception is thrown TEN TIMES when
    // the pns tree is first opened - and there are no selected objects :
    //java.lang.IllegalArgumentException: select at lease two parameterizations for compare
    //at org.pillarone.riskanalytics.application.ui.main.action.CompareParameterizationsAction.validate(CompareParameterizationsAction.groovy:38)
    //at org.pillarone.riskanalytics.application.ui.main.action.CompareParameterizationsAction.isEnabled(CompareParameterizationsAction.groovy:51)
    //at com.ulcjava.base.application.ULCAbstractButton.configureDefaultPropertiesFromAction(ULCAbstractButton.java:156)
    //at com.ulcjava.base.application.ULCMenuItem.configurePropertiesFromAction(ULCMenuItem.java:37)
    //at com.ulcjava.base.application.ULCAbstractButton.setAction(ULCAbstractButton.java:176)
    //at com.ulcjava.base.application.ULCMenuItem.<init>(ULCMenuItem.java:16)
    //
    //Clearly, this method should NOT be calling validate() to decide whether the action is enabled.
    //
    //In fact, it works fine without overriding this method.
//    public boolean isEnabled() {
//        List elements = getSelectedObjects(Parameterization.class)
//        try {
//            validate(elements)
//        } catch (IllegalArgumentException ex) {
//            LOG.error("isEnabled(): validate(${elements.size()} elements) threw.. " + ex.message)
//            return false
//        }
//        return true
//    }

}
