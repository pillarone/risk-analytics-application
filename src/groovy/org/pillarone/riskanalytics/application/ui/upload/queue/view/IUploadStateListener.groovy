package org.pillarone.riskanalytics.application.ui.upload.queue.view

import org.pillarone.riskanalytics.core.upload.UploadState

interface IUploadStateListener {
    void uploadStateChanged(UploadState uploadState)
}
