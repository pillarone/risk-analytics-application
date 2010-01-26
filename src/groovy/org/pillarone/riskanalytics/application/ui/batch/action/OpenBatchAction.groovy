package org.pillarone.riskanalytics.application.ui.batch.action

import org.pillarone.riskanalytics.core.output.batch.BatchRunner

import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.BatchRun

/**
 * @author fouad jaada
 */

public class OpenBatchAction extends SelectionTreeAction {

    public OpenBatchAction(ULCTree tree, P1RATModel model) {
        super("OpenBatch", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        //Model model = getSelectedModel()
        def item = getSelectedItem()
        if (item != null) {
            item = BatchRun.findByName(item.name)
            this.model.openItem(null, item)
        }
    }

}

public class NewBatchAction extends SelectionTreeAction {

    public NewBatchAction(ULCTree tree, P1RATModel model) {
        super("NewBatch", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        BatchRun batchRun = new BatchRun()
        this.model.openItem(null, batchRun)
    }

}

public class RunBatchAction extends SelectionTreeAction {

    public RunBatchAction(ULCTree tree, P1RATModel model) {
        super("RunBatch", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        def batchToRun = getSelectedItem()
        if (batchToRun != null) {
            batchToRun = BatchRun.findByName(batchToRun.name)
            if (!batchToRun.executed) {
                BatchRunner.getService().runBatch(batchToRun)
            } else {
                new I18NAlert("BatchAlreadyExecuted").show()
            }
        }
    }

}

public class DeleteBatchAction extends SelectionTreeAction {

    public DeleteBatchAction(ULCTree tree, P1RATModel model) {
        super("DeleteBatch", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        def item = getSelectedItem()
        if (item != null) {
            model.removeItem(item)
        }
    }

}
