package org.pillarone.riskanalytics.application.ui.batch.view.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView

class DeleteParameterizationsAction extends ResourceBasedAction {
    private final BatchView batchView

    DeleteParameterizationsAction(BatchView batchView) {
        super('DeleteParameterizations')
        this.batchView = batchView
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        batchView.removeSelectedParameterizations()
    }

    @Override
    boolean isEnabled() {
        !batchView.batch.executed && batchView.selectedBatchRowInfos.size() > 0
    }
}
