package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationSettingsUIItem
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsChangedEvent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 *
 * Note: This action executes off the "Run simulation..." menu on a p14n.  It opens up the simulation pane.
 * (The RunSimulationAction presumably executes off the Run button on the simulation pane.)
 */
class SimulationAction extends SingleItemAction {

    private final static Log LOG = LogFactory.getLog(SimulationAction)

    SimulationAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("RunSimulation", tree, model)
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, true));
    }

    void doActionPerformed(ActionEvent event) {
        Model selectedModel = selectedModel
        if (selectedModel) {
            Object selectedItem = selectedItem
            Simulation simulation = new Simulation('Simulation')
            simulation.modelClass = selectedModel.modelClass
            Parameterization parameterization = selectedItem instanceof Parameterization ? selectedItem : null
            ResultConfiguration template = selectedItem instanceof ResultConfiguration ? selectedItem : null
            model.notifyOpenDetailView(new SimulationSettingsUIItem(simulation))
            model.post(new SimulationSettingsChangedEvent(template, parameterization, simulation.modelClass))
        } else {
            LOG.debug("No selected model found. Action cancelled.")
        }
    }
}
