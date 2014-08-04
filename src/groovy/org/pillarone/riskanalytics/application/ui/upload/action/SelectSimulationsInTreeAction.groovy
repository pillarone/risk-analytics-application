package org.pillarone.riskanalytics.application.ui.upload.action

import org.pillarone.riskanalytics.application.ui.batch.action.AbstractSelectItemsInTreeAction
import org.pillarone.riskanalytics.application.ui.upload.view.UploadBatchView
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SelectSimulationsInTreeAction extends AbstractSelectItemsInTreeAction<Simulation> {

    static final String ACTION_NAME = 'FindResultsInTree'

    private final UploadBatchView uploadBatchView

    SelectSimulationsInTreeAction(UploadBatchView uploadBatchView) {
        super(ACTION_NAME)
        this.uploadBatchView = uploadBatchView
    }

    protected List<Simulation> getItems() {
        uploadBatchView.selectedSimulationRowInfos.simulation - [null]
    }

    @Override
    boolean isEnabled() {
        uploadBatchView.selectedSimulationRowInfos.size() > 0
    }
}
