package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class OpenItemAction extends BatchSimulationSelectionAction {
    final static int SIMULATION = 0
    final static int PARAMETERIZATION = 1
    final static int RESULT_CONFIG = 2
    int actionId = 0
    RiskAnalyticsMainModel mainModel

    public OpenItemAction(RiskAnalyticsMainModel mainModel, BatchDataTableModel model, int actionId, String actionName) {
        super(actionName);
        this.mainModel = mainModel
        super.@model = model
        this.actionId = actionId
    }

    public void doActionPerformed(ActionEvent event) {
        SimulationRun run = getSelectedSimulationRun()
        Simulation simulation = new Simulation(run.name)
        Class modelClass = getClass().getClassLoader().loadClass(run.model)
        simulation.modelClass = modelClass
        open(modelClass.newInstance(), simulation)
    }

    private void open(Model itemModel, Simulation simulation) {
        simulation.load();
        switch (actionId) {
            case SIMULATION: openItem(itemModel, simulation); break;
            case PARAMETERIZATION: simulation.parameterization.load(); openItem(itemModel, simulation.parameterization); break;
            case RESULT_CONFIG: simulation.template.load(); openItem(itemModel, simulation.template);
        }
    }

    private void openItem(Model itemModel, Simulation item) {
        if (item.simulationRun.endTime) {
            itemModel.init()
            mainModel.notifyOpenDetailView(itemModel, item)
        } else {
            new I18NAlert("SimulationNotexecuted").show()
        }
    }

    private void openItem(Model itemModel, ModellingItem item) {
        itemModel.init()
        item.dao.modelClassName = model.class.name
        mainModel.notifyOpenDetailView(itemModel, item)
    }

}
