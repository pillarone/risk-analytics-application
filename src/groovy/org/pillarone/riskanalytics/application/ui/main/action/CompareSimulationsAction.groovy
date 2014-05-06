package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.CompareSimulationUIItem
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareSimulationsAction extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(CompareSimulationsAction)

    public CompareSimulationsAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("CompareSimulations", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        List<SimulationNode> elements = getSelectedObjects(Simulation.class) as List<SimulationNode>
        try {
            validate(elements)
            Model selectedModel = getSelectedModel(elements[0])
            selectedModel.init()
            if (selectedModel != null) {
                List<Simulation> items = elements*.itemNodeUIItem.item as List<Simulation>
                CompareSimulationUIItem uiItem = new CompareSimulationUIItem(selectedModel, items)
                model.notifyOpenDetailView(uiItem)
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex) // logged and shown in alert by ExceptionSafe
        }
    }

    private void validate(List<SimulationNode> elements) throws IllegalArgumentException, Exception {
        if (elements.size() < 2) {
            throw new IllegalArgumentException("Please select two or more sims to compare (Also pls tell developers how you managed to get this error ?!)")
        }
        //throw new IllegalArgumentException("Forced validation failure test") tested 20140104 - this DOES appear in a popup alert
        Class modelClass = elements[0].itemNodeUIItem.item.modelClass
        elements.each {
            if (it.itemNodeUIItem.item.modelClass != modelClass) {
                throw new IllegalArgumentException("Cannot compare simulations with differing ModelClass")
            }
        }
        // This check moved from isEnabled()
        Model selectedModel = getSelectedModel(elements[0])
        //at moment compare of deterministic models not supported
        if (selectedModel instanceof DeterministicModel) {
            throw new IllegalArgumentException("Comparing deterministic models not supported")
        }
    }

    public boolean isEnabled() {

        if (getSelectedObjects(Simulation.class).size() < 2) {
            return false;
        }

        return super.isEnabled() //Does generic and user roles check etc
    }

}