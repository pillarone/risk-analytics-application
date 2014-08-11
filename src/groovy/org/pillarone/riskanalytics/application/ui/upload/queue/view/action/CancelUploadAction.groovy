package org.pillarone.riskanalytics.application.ui.upload.queue.view.action

import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.upload.queue.view.UploadQueueView
import org.pillarone.riskanalytics.core.upload.UploadQueueService
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class CancelUploadAction extends ResourceBasedAction {
    private final static Log LOG = LogFactory.getLog(CancelUploadAction)

    @Resource
    UploadQueueService uploadQueueService
    @Resource
    UploadQueueView uploadQueueView

    CancelUploadAction() {
        super('CancelSimulation')
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            uploadQueueView.selectedUploads.each {
                String simulationName = it?.getSimulation()?.getName();
                if(!simulationName){
                    simulationName="<unknown>"
                }
                LOG.info("Canceling queued sim: $simulationName")
                uploadQueueService.cancel(it.id)
            }
        }
    }

    @Override
    boolean isEnabled() {
        uploadQueueView.selectedUploads.size() > 0
    }
}
