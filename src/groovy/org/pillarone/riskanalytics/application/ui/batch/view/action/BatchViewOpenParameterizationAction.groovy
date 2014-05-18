package org.pillarone.riskanalytics.application.ui.batch.view.action

import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class BatchViewOpenParameterizationAction extends AbstractOpenParameterizationAction {
    private final BatchView batchView

    BatchViewOpenParameterizationAction(BatchView batchView) {
        this.batchView = batchView
    }

    @Override
    protected Parameterization getParameterization() {
        batchView.selectedBatchRowInfos.first().parameterization
    }

    @Override
    boolean isEnabled() {
        batchView.selectedBatchRowInfos.size() == 1
    }
}
