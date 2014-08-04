package org.pillarone.riskanalytics.application.ui.upload.action

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.batch.action.AbstractOpenItemAction
import org.pillarone.riskanalytics.application.ui.upload.view.UploadBatchView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

@CompileStatic
class OpenParameterizationAction extends AbstractOpenItemAction<Parameterization> {
    private final UploadBatchView uploadBatchView

    OpenParameterizationAction(UploadBatchView uploadBatchView) {
        super('OpenParameterizationAction')
        this.uploadBatchView = uploadBatchView
    }

    @Override
    protected Parameterization getModellingItem() {
        uploadBatchView.selectedSimulationRowInfos.first().parameterization
    }

    @Override
    boolean isEnabled() {
        uploadBatchView.selectedSimulationRowInfos.size() == 1
    }
}
