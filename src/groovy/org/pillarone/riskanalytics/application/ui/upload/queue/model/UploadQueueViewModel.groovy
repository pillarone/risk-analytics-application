package org.pillarone.riskanalytics.application.ui.upload.queue.model

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.core.queue.IRuntimeInfoListener
import org.pillarone.riskanalytics.core.upload.UploadQueueService
import org.pillarone.riskanalytics.core.upload.UploadRuntimeInfo
import org.pillarone.riskanalytics.core.upload.UploadRuntimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class UploadQueueViewModel {
    @Resource
    UlcUploadRuntimeService ulcUploadRuntimeService
    @Autowired
    UploadRuntimeService uploadRuntimeService
    @Resource
    UploadQueueTableModel uploadQueueTableModel
    @Autowired
    UploadQueueService uploadQueueService

    private final IRuntimeInfoListener infoListener = new MyInfoListener()

    @PostConstruct
    void initialize() {
        ulcUploadRuntimeService.addRuntimeInfoListener(infoListener)
        uploadQueueTableModel.infos = uploadRuntimeService.queued
    }

    @PreDestroy
    void unregister() {
        ulcUploadRuntimeService.removeRuntimeInfoListener(infoListener)
    }

    List<UploadRuntimeInfo> getInfoAt(int[] selected) {
        selected.collect {
            uploadQueueTableModel.getInfoAt(it)
        }
    }

    private class MyInfoListener implements IRuntimeInfoListener<UploadRuntimeInfo> {

        @Override
        void starting(UploadRuntimeInfo info) {
            uploadQueueTableModel.itemChanged(info)

        }

        @Override
        void finished(UploadRuntimeInfo info) {
//            uploadQueueTableModel.itemRemoved(info)
            //TODO enable
            println("in future this info will be removed ...")
        }

        @Override
        void removed(UploadRuntimeInfo info) {
            uploadQueueTableModel.itemRemoved(info)

        }

        @Override
        void offered(UploadRuntimeInfo info) {
            uploadQueueTableModel.itemAdded(info)

        }

        @Override
        void changed(UploadRuntimeInfo info) {
            uploadQueueTableModel.itemChanged(info)
        }
    }
}
