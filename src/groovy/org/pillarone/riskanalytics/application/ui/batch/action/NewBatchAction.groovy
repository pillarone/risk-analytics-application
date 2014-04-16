package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.core.simulation.item.Batch

import static org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem.NEWBATCH

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class NewBatchAction extends SelectionTreeAction {

    NewBatchAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("NewBatch", tree, model)
    }

    void doActionPerformed(ActionEvent event) {
        this.model.openItem(null, new BatchUIItem(model, new Batch(NEWBATCH)))
    }
}
