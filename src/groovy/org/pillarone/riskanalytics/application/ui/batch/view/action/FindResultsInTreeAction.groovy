package org.pillarone.riskanalytics.application.ui.batch.view.action

import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class FindResultsInTreeAction extends AbstractFindResultsInTreeAction {
    private final BatchView batchView

    FindResultsInTreeAction(BatchView batchView) {
        this.batchView = batchView
    }

    protected List<Simulation> getSimulations() {
        batchView.selectedBatchRowInfos.simulation - [null]
    }

    @Override
    boolean isEnabled() {
        batchView.selectedBatchRowInfos.size() > 0
    }
}
