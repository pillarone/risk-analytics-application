package org.pillarone.riskanalytics.application.ui.upload.queue.model

import org.pillarone.riskanalytics.application.ui.upload.queue.view.IUploadStateListener
import org.pillarone.riskanalytics.core.upload.UploadState

class UploadStateEventSupport {

    private Set<IUploadStateListener> listeners = Collections.synchronizedSet([] as Set)

    void addUploadStateListener(IUploadStateListener listener) {
        listeners.add(listener)
    }

    void removeUploadStateListener(IUploadStateListener listener) {
        listeners.add(listener)
    }

    void notifyUploadStateChanged(UploadState uploadState) {
        listeners.each { it.uploadStateChanged(uploadState) }
    }
}
