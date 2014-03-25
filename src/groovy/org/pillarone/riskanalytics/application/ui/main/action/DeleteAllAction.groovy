package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
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

    protected void deleteParameterizations(List<Parameterization> items) {
        //Parameterization
        boolean usedInSimulation = items.any { Parameterization parameterization ->
            parameterization.usedInSimulation
        }
        if (usedInSimulation) {
            ULCAlert alert = new I18NAlert("DeleteAllError")
            alert.show()
        } else {
            //TODO won't work anymore
            model.removeItems(selectedModel, items)
        }
    }

    protected void deleteResultConfigurations(List<ResultConfiguration> items) {
        boolean usedInSimulation = items.any { ResultConfiguration resultConfiguration ->
            resultConfiguration.usedInSimulation
        }
        if (usedInSimulation) {
            ULCAlert alert = new I18NAlert("DeleteAllError")
            alert.show()
        } else {
            //TODO won't work anymore
            model.removeItems(selectedModel, items)
        }
    }

    protected void deleteResults(List<Simulation> items) {
        //TODO won't work anymore
        model.removeItems(selectedModel, items)
    }

    @Override
    boolean isEnabled() {
        //TODO remove code or fix and enable again
        return false
    }
}
