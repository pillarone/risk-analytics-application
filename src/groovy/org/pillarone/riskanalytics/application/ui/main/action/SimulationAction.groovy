package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationSettingsUIItem
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

    public SimulationAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("RunSimulation", tree, model)
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, true));
    }

    public void doActionPerformed(ActionEvent event) {

// Only do this for dangerous actions; running a sim just opens the sim window.
//        if( quitWithAlertIfCalledWhenDisabled() ){
//            return
//        }

        Model selectedModel = selectedModel
        if (selectedModel) {
            Object selectedItem = selectedItem
            Simulation simulation = new Simulation("Simulation")
            simulation.parameterization = selectedItem instanceof Parameterization ? selectedItem : null
            simulation.template = selectedItem instanceof ResultConfiguration ? selectedItem : null
            simulation.modelClass = selectedModel.modelClass
            model.openItem(selectedModel, new SimulationSettingsUIItem(selectedModel, simulation))
            model.fireNewSimulation(simulation)
        } else {
            LOG.debug("No selected model found. Action cancelled.")
        }
    }
}
