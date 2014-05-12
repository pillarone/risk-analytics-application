package org.pillarone.riskanalytics.application.ui.batch.action

import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SelectSimulationsInTreeAction extends AbstractSelectItemsInTreeAction<Simulation> {

    static final String ACTION_NAME = 'FindResultsInTree'

    private final BatchView batchView

    SelectSimulationsInTreeAction(BatchView batchView) {
        super(ACTION_NAME)
        this.batchView = batchView
    }

    protected List<Simulation> getItems() {
        batchView.selectedBatchRowInfos.simulation - [null]
    }

    @Override
    boolean isEnabled() {
        batchView.selectedBatchRowInfos.size() > 0
    }
}
