package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.batch.action.DeleteBatchAction
import org.pillarone.riskanalytics.application.ui.batch.action.NewBatchAction
import org.pillarone.riskanalytics.application.ui.batch.action.OpenBatchAction
import org.pillarone.riskanalytics.application.ui.batch.action.RunBatchAction
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem

/**
 * @author fouad jaada
 */

public class BatchRunNode extends ItemNode {

    public BatchRunNode(BatchUIItem batchUIItem) {
        super(batchUIItem, true, true)
    }

    @Override
    public ULCPopupMenu getPopupMenu( ULCTableTree tree) {
        ULCPopupMenu batchesNodePopUpMenu = new ULCPopupMenu()
        batchesNodePopUpMenu.name = "batchesNodePopUpMenu"
        batchesNodePopUpMenu.add(new ULCMenuItem(new OpenBatchAction(tree, abstractUIItem.mainModel)))
        batchesNodePopUpMenu.add(new ULCMenuItem(new NewBatchAction(tree, abstractUIItem.mainModel)))
        batchesNodePopUpMenu.add(new ULCMenuItem(new RunBatchAction(tree, abstractUIItem.mainModel)))
        batchesNodePopUpMenu.addSeparator()
        batchesNodePopUpMenu.add(new ULCMenuItem(new DeleteBatchAction(tree, (BatchUIItem) abstractUIItem)))
        return batchesNodePopUpMenu
    }

}

