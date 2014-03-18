package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class DeleteAllAction extends SelectionTreeAction {

    public DeleteAllAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("DeleteAll", tree, model)
    }

    protected void deleteParameterizations(List items) {
        boolean usedInSimulation = false
        //Parameterization
        for (Parameterization parameterization: items) {
            usedInSimulation = parameterization.isUsedInSimulation()
            if (usedInSimulation == true)
                break
        }
        if (!usedInSimulation) {
            model.removeItems(getSelectedModel(), items)
        } else {
            ULCAlert alert = new I18NAlert("DeleteAllError")
            alert.show()
        }
    }

    protected void deleteResultConfigurations(List items) {
        //ResultTemplate
        boolean usedInSimulation = false
        for (ResultConfiguration resultConfiguration: items) {
            usedInSimulation = resultConfiguration.isUsedInSimulation()
            if (usedInSimulation == true)
                break
        }
        if (!usedInSimulation) {
            model.removeItems(getSelectedModel(), items)
        } else {
            ULCAlert alert = new I18NAlert("DeleteAllError")
            alert.show()
        }
    }

    protected void deleteResults(List items) {
        model.removeItems(getSelectedModel(), items)
    }

    private boolean isInList(List<Simulation> simulations, Parameterization parameterization) {
        if (simulations == null || simulations.size() == 0) return false
        def result = simulations.any {
            it.parameterization == parameterization
        }
        return result
    }


    private void removeAllChildren(Model selectedModel, ModellingItem selectedItem) {
        model.navigationTableTreeModel.removeAllNodeForItem(selectedItem)
        model.fireModelChanged()
    }

    @Override
    boolean isEnabled() {
        return false
    }


}
