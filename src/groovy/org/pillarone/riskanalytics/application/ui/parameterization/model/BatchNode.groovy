package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.main.action.DeleteAction
import org.pillarone.riskanalytics.application.ui.main.action.OpenItemAction
import org.pillarone.riskanalytics.application.ui.main.action.RenameAction
import org.pillarone.riskanalytics.application.ui.main.action.RunBatchAction
import org.pillarone.riskanalytics.application.ui.main.view.EnabledCheckingMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad jaada
 * @author simon parten
 */
class BatchNode extends ItemNode implements IReportableNode {

    static final String BATCHES_NODE_POP_UP_MENU = 'batchesNodePopUpMenu'

    BatchNode(BatchUIItem batchUIItem) {
        super(batchUIItem, true)
    }

    @Override
    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        ULCPopupMenu batchesNodePopUpMenu = new ULCPopupMenu()
        batchesNodePopUpMenu.name = BATCHES_NODE_POP_UP_MENU
        batchesNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree)))
        batchesNodePopUpMenu.add(new EnabledCheckingMenuItem(new RunBatchAction(tree)))
        batchesNodePopUpMenu.addSeparator()
        batchesNodePopUpMenu.add(new EnabledCheckingMenuItem(new RenameAction(tree)))
        batchesNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree)))
        addReportMenus(batchesNodePopUpMenu, tree, true)
        return batchesNodePopUpMenu
    }

    /**
     *
     * @return a list of (model) classes which are used in this batch job
     *
     */
    @Override
    List<Class> modelsToReportOn() {
        itemNodeUIItem.item.parameterizations.modelClass
    }

    @Override
    List<Simulation> modellingItemsForReport() {
        itemNodeUIItem.item.simulations
    }

    @Override
    BatchUIItem getItemNodeUIItem() {
        return super.itemNodeUIItem as BatchUIItem
    }
}

