package org.pillarone.riskanalytics.application.ui.batch.action

import org.pillarone.riskanalytics.core.output.batch.BatchRunner

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Simulation

public class RunBatchSimulationAction extends BatchSimulationSelectionAction {

    public RunBatchSimulationAction() {
        super("RunBatchSimulation");
    }

    public void doActionPerformed(ActionEvent event) {
        BatchRunSimulationRun batchRunSimulationRun = BatchRunner.getService().getSimulationRun(model.batchRun, getSelectedSimulationRun())
        BatchRunner.getService().runSimulation(batchRunSimulationRun)
    }


}


public class DeleteBatchSimulationAction extends BatchSimulationSelectionAction {

    public DeleteBatchSimulationAction() {
        super("DeleteBatchSimulation");
    }

    public void doActionPerformed(ActionEvent event) {
        SimulationRun run = getSelectedSimulationRun()
        int rowIndex = model.getRowIndex(run)
        if (rowIndex != -1) {
            BatchRunner.getService().deleteSimulationRun(model.batchRun, run)
            model.fireRowDeleted(rowIndex)
        }
    }

}

public class ChangeBatchSimulationPriorityAction extends BatchSimulationSelectionAction {

    int step

    public ChangeBatchSimulationPriorityAction(BatchDataTableModel model, int step) {
        super("ChangeBatchSimulationPriorityAction" + (step > 0 ? "ToBottom" : "ToTop"));
        super.@model = model
        this.@step = step;

    }

    public ChangeBatchSimulationPriorityAction(int step) {
        super("ChangeBatchSimulationPriorityAction" + (step > 0 ? "ToTop" : "ToBottom"));
        this.step = step
    }

    public void doActionPerformed(ActionEvent event) {
        SimulationRun run = getSelectedSimulationRun()
        int rowIndex = model.getRowIndex(run)
        if (rowIndex != -1) {
            model.batchRun.batchRunService.changePriority(model.batchRun, run, step)
            model.firePriorityChanged rowIndex, step
        }
    }

}

public class OpenItemAction extends BatchSimulationSelectionAction {
    final static int SIMULATION = 0
    final static int PARAMETERIZATION = 1
    final static int RESULT_CONFIG = 2
    int actionId = 0

    public OpenItemAction(BatchDataTableModel model, int actionId, String actionName) {
        super(actionName);
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
            model.openDetailView itemModel, item
        } else {
            new I18NAlert("SimulationNotexecuted").show()
        }
    }

    private void openItem(Model itemModel, ModellingItem item) {
        itemModel.init()
        item.dao.modelClassName = model.class.name
        model.openDetailView itemModel, item
    }

}




abstract class BatchSimulationSelectionAction extends ResourceBasedAction {
    protected BatchDataTableModel model

    public BatchSimulationSelectionAction(String actionName) {
        super(actionName);
    }

    SimulationRun getSelectedSimulationRun() {
        SimulationRun run = model?.selectedRun
        return run
    }

}
