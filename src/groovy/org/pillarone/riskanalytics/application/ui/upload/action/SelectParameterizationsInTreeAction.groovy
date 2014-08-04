package org.pillarone.riskanalytics.application.ui.upload.action

import org.pillarone.riskanalytics.application.ui.batch.action.AbstractSelectItemsInTreeAction
import org.pillarone.riskanalytics.application.ui.upload.view.UploadBatchView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class SelectParameterizationsInTreeAction extends AbstractSelectItemsInTreeAction<Parameterization> {
    static final String ACTION_NAME = 'FindParameterizationsInTree'
    private final UploadBatchView uploadBatchView

    SelectParameterizationsInTreeAction(UploadBatchView uploadBatchView) {
        super(ACTION_NAME)
        this.uploadBatchView = uploadBatchView
    }

    @Override
    protected List<Parameterization> getItems() {
        uploadBatchView.selectedSimulationRowInfos.parameterization
    }

    @Override
    boolean isEnabled() {
        uploadBatchView.selectedSimulationRowInfos.size() > 0
    }
}
