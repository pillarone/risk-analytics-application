package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

@CompileStatic
class OpenBatchAction extends SelectionTreeAction {

    OpenBatchAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("OpenBatch", tree, model)
    }

    void doActionPerformed(ActionEvent event) {
        model.openItem(null, selectedUIItem)
    }
}




