package org.pillarone.riskanalytics.application.ui.main.view.item

import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.upload.view.UploadBatchView
import org.pillarone.riskanalytics.core.model.Model

class UploadBatchUIItem extends AbstractUIItem<UploadBatchView> {

    @Override
    UploadBatchView createDetailView() {
        Holders.grailsApplication.mainContext.getBean('uploadBatchView', UploadBatchView)
    }

    @Override
    String createTitle() {
        'Upload simulation results'
    }

    @Override
    String getWindowTitle() {
        'Upload simulation results'
    }

    @Override
    Model getModel() {
        null
    }

    @Override
    boolean equals(Object obj) {
        return obj instanceof UploadBatchUIItem
    }

    @Override
    int hashCode() {
        return 0
    }
}
