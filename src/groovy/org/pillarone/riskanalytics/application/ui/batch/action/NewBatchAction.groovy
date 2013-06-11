package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
public class NewBatchAction extends SelectionTreeAction {

    public NewBatchAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("NewBatch", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        this.model.openItem(null, new BatchUIItem(model,  null))
    }

}
