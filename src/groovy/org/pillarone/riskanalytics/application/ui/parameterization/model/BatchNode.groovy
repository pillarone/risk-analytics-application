package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.batch.action.RunBatchAction
import org.pillarone.riskanalytics.application.ui.main.action.DeleteAction
import org.pillarone.riskanalytics.application.ui.main.action.OpenItemAction
import org.pillarone.riskanalytics.application.ui.main.action.RenameAction
import org.pillarone.riskanalytics.application.ui.main.view.EnabledCheckingMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad jaada
 * @author simon parten
 */
@CompileStatic
class BatchNode extends ItemNode implements IReportableNode {

    static final String BATCHES_NODE_POP_UP_MENU = 'batchesNodePopUpMenu'

    BatchNode(BatchUIItem batchUIItem) {
        super(batchUIItem, true)
    }

    @Override
    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        ULCPopupMenu batchesNodePopUpMenu = new ULCPopupMenu()
        batchesNodePopUpMenu.name = BATCHES_NODE_POP_UP_MENU
        batchesNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, riskAnalyticsMainModel)))
        batchesNodePopUpMenu.add(new EnabledCheckingMenuItem(new RunBatchAction(tree, riskAnalyticsMainModel)))
        batchesNodePopUpMenu.addSeparator()
        batchesNodePopUpMenu.add(new EnabledCheckingMenuItem(new RenameAction(tree, riskAnalyticsMainModel)))
        batchesNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, riskAnalyticsMainModel)))
        addReportMenus(batchesNodePopUpMenu, tree, true)
        return batchesNodePopUpMenu
    }

    /**
     *
     * @return a list of (model) classes which are used in this batch job
     */
    @Override
    List<Class> modelsToReportOn() {
        //TODO
        Batch batch = itemNodeUIItem.item
        return batch.executed ? batch.simulations.collect { Simulation simulation -> simulation.modelClass } : []
    }

    /**
     * For the menu to have been generated, the batch must have been executed so we can trust that condition.
     *
     * @return List of Simulations to report on.
     */
    @Override
    List<Simulation> modellingItemsForReport() {
        //TODO
        Batch batch = itemNodeUIItem.item
        batch.simulations
    }

    @Override
    BatchUIItem getItemNodeUIItem() {
        return super.itemNodeUIItem as BatchUIItem
    }
}

