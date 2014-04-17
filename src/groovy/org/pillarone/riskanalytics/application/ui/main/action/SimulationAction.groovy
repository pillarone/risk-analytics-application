package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.CalculationSettingsUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationSettingsUIItem
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SimulationAction extends SelectionTreeAction {

    private final static Log LOG = LogFactory.getLog(SimulationAction)

    public SimulationAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("RunSimulation", tree, model)
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, true));
    }

    public void doActionPerformed(ActionEvent event) {
        Model selectedModel = selectedModel
        if (selectedModel) {
            Object selectedItem = selectedItem
            Simulation simulation = new Simulation("Simulation")
            simulation.parameterization = selectedItem instanceof Parameterization ? selectedItem : null
            simulation.template = selectedItem instanceof ResultConfiguration ? selectedItem : null
            model.openItem(selectedModel, getUIItemByModel(selectedModel, simulation))
            model.fireNewSimulation(simulation)
        } else {
            LOG.debug("No selected model found. Action cancelled.")
        }
    }

    SimulationSettingsUIItem getUIItemByModel(Model selectedModel, Simulation simulation) {
        if (selectedModel instanceof StochasticModel) {
            return new SimulationSettingsUIItem(selectedModel, simulation)
        } else {
            return new CalculationSettingsUIItem(model, selectedModel, simulation)
        }
    }

}
