package org.pillarone.riskanalytics.application.ui.upload.finished.model

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.eventbus.RiskAnalyticsEventBus
import org.pillarone.riskanalytics.application.ui.upload.queue.model.UlcUploadRuntimeService
import org.pillarone.riskanalytics.core.queue.IRuntimeInfoListener
import org.pillarone.riskanalytics.core.upload.UploadRuntimeInfo
import org.pillarone.riskanalytics.core.upload.UploadRuntimeInfoAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedUploadsViewModel {
    @Resource
    UlcUploadRuntimeService ulcUploadRuntimeService
    @Autowired
    RiskAnalyticsEventBus riskAnalyticsEventBus
    @Resource
    FinishedUploadsTableModel finishedUploadsTableModel

    private final IRuntimeInfoListener infoListener = new MyInfoListener()

    @PostConstruct
    void initialize() {
        ulcUploadRuntimeService.addRuntimeInfoListener(infoListener)
    }

    @PreDestroy
    void unregister() {
        ulcUploadRuntimeService.removeRuntimeInfoListener(infoListener)
    }

    List<UploadRuntimeInfo> getInfoAt(int[] selected) {
        selected.collect {
            finishedUploadsTableModel.getInfoAt(it)
        }
    }

    void clearAll() {
        finishedUploadsTableModel.infos = []
    }

    void removeAt(int[] selected) {
        finishedUploadsTableModel.removeAt(selected)
    }

    private class MyInfoListener extends UploadRuntimeInfoAdapter {
        @Override
        void finished(UploadRuntimeInfo info) {
            finishedUploadsTableModel.itemAdded(info)
        }
    }
}
