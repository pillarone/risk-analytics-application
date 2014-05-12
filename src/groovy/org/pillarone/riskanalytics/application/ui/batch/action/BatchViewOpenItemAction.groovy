package org.pillarone.riskanalytics.application.ui.batch.action

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

@CompileStatic
class BatchViewOpenItemAction extends AbstractOpenItemAction<Parameterization> {
    private final BatchView batchView

    BatchViewOpenItemAction(BatchView batchView) {
        super('BatchOpenParameterizationAction')
        this.batchView = batchView
    }

    @Override
    protected Parameterization getModellingItem() {
        batchView.selectedBatchRowInfos.first().parameterization
    }

    @Override
    boolean isEnabled() {
        batchView.selectedBatchRowInfos.size() == 1
    }
}
