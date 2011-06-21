package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

/**
 * @author fouad jaada
 */

public class OpenBatchAction extends SelectionTreeAction {

    public OpenBatchAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("OpenBatch", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        model.openItem(null, getSelectedUIItem())
    }

}




