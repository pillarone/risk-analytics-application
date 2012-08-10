package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.batch.action.NewBatchAction
import org.pillarone.riskanalytics.application.ui.batch.action.OpenBatchAction
import org.pillarone.riskanalytics.application.ui.batch.action.RunBatchAction
import org.pillarone.riskanalytics.application.ui.main.action.DeleteAction
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel

import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry

import org.pillarone.riskanalytics.core.simulation.item.Simulation

import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.BatchRunSimulationRun

/**
 * @author fouad jaada
 * @author simon parten
 */

public class BatchRunNode extends ItemNode implements IReportableNode {

    public BatchRunNode(BatchUIItem batchUIItem) {
        super(batchUIItem, true, true)
    }

    @Override
    public ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        ULCPopupMenu batchesNodePopUpMenu = new ULCPopupMenu()
        batchesNodePopUpMenu.name = "batchesNodePopUpMenu"
        batchesNodePopUpMenu.add(new ULCMenuItem(new OpenBatchAction(tree, abstractUIItem.mainModel)))
        batchesNodePopUpMenu.add(new ULCMenuItem(new NewBatchAction(tree, abstractUIItem.mainModel)))
        batchesNodePopUpMenu.add(new ULCMenuItem(new RunBatchAction(tree, abstractUIItem.mainModel)))
        batchesNodePopUpMenu.addSeparator()
        batchesNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, abstractUIItem.mainModel)))
        addReportMenus(batchesNodePopUpMenu, tree, true)
        return batchesNodePopUpMenu
    }

    /**
     *
     * @return a list of (model) classes which are used in this batch job
     */
    List<Class> modelsToReportOn() {
        List<Class> classes = new ArrayList<Class>()
        BatchRun batchRun = BatchRun.findByName( abstractUIItem.name )
        if(batchRun == null){
            throw new IllegalStateException("Failed to lookup batch run in DB. Please report to development")
        }
        List<SimulationRun> batchRunSimulationRuns = BatchRunSimulationRun.findAllByBatchRun(batchRun, [sort: "priority", order: "asc"])*.getSimulationRun()

        List<String> modelNames = batchRunSimulationRuns*.model.unique()
        if (batchRun.executed) {
            ModelRegistry modelRegistry = ModelRegistry.getInstance()
            for (String model in modelNames) {
                classes << modelRegistry.getModelClass(model)
            }
        }
        return classes
    }

    /**
     * For the menu to have been generated, the batch must have been executed so we can trust that condition.
     *
     * @return List of Simulations to report on.
     */
    List<ModellingItem> modellingItemsForReport() {

        BatchRun batchRun = BatchRun.findByName( abstractUIItem.name )
        if(batchRun == null){
            throw new IllegalStateException("Failed to lookup batch run in DB. Please report to development")
        }
        List<SimulationRun> batchRunSimulationRuns = BatchRunSimulationRun.findAllByBatchRun(batchRun, [sort: "priority", order: "asc"])*.getSimulationRun()
        ArrayList<ModellingItem> simulationList = new ArrayList<ModellingItem>()
        for (SimulationRun run in batchRunSimulationRuns) {
            Simulation simulation = new Simulation(run.name)
            simulationList << simulation
        }
        return simulationList
    }
}

