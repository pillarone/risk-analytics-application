package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.action.AbstractUIItemAction
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class DeleteBatchAction extends AbstractUIItemAction {

    public DeleteBatchAction(ULCTableTree tree, BatchUIItem batchUIItem) {
        super("DeleteBatch", tree, batchUIItem)
    }

    public void doActionPerformed(ActionEvent event) {
        AbstractUIItem abstractUIItem = getSelectedUIItem()
        if (abstractUIItem != null) {
            abstractUIItem.remove()
        }
    }

}
