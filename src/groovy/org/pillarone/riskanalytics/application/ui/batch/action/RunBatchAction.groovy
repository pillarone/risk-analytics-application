package org.pillarone.riskanalytics.application.ui.batch.action
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class RunBatchAction extends SelectionTreeAction {

    public RunBatchAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("RunBatch", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        Batch batchToRun = selectedItem as Batch
        if (batchToRun != null) {
            if (batchToRun.executed) {
                new I18NAlert("BatchAlreadyExecuted").show()
            } else {
                BatchRunService.service.runBatch(batchToRun)
            }
        }
    }
}
