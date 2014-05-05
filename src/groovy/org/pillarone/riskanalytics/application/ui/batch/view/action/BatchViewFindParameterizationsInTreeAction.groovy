package org.pillarone.riskanalytics.application.ui.batch.view.action

import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class BatchViewFindParameterizationsInTreeAction extends AbstractFindParameterizationsInTreeAction {
    private final BatchView batchView

    BatchViewFindParameterizationsInTreeAction(BatchView batchView) {
        this.batchView = batchView
    }

    @Override
    protected List<Parameterization> getParameterizations() {
        batchView.selectedBatchRowInfos.parameterization
    }

    @Override
    boolean isEnabled() {
        batchView.selectedBatchRowInfos.size() > 0
    }
}
