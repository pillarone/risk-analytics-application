package org.pillarone.riskanalytics.application.ui.batch.action

import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class SelectParameterizationsInTreeAction extends AbstractSelectItemsInTreeAction<Parameterization> {
    static final String ACTION_NAME = 'FindParameterizationsInTree'
    private final BatchView batchView

    SelectParameterizationsInTreeAction(BatchView batchView) {
        super(ACTION_NAME)
        this.batchView = batchView
    }

    @Override
    protected List<Parameterization> getItems() {
        batchView.selectedBatchRowInfos.parameterization
    }

    @Override
    boolean isEnabled() {
        batchView.selectedBatchRowInfos.size() > 0
    }
}
