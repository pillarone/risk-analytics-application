package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.batch.BatchRunService

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class RunBatchAction extends SelectionTreeAction {

    public RunBatchAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("RunBatch", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        def batchToRun = selectedItem as BatchRun
        if (batchToRun != null) {
            batchToRun = BatchRun.findByName(batchToRun.name)
            if (batchToRun.executed) {
                new I18NAlert("BatchAlreadyExecuted").show()
            } else {
                BatchRunService.service.runBatch(batchToRun)
            }
        }
    }

}
