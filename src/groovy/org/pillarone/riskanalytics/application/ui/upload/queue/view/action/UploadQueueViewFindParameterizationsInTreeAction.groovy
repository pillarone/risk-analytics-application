package org.pillarone.riskanalytics.application.ui.upload.queue.view.action

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.batch.action.AbstractSelectItemsInTreeAction
import org.pillarone.riskanalytics.application.ui.upload.queue.view.UploadQueueView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class UploadQueueViewFindParameterizationsInTreeAction extends AbstractSelectItemsInTreeAction<Parameterization> {

    static final String ACTION_NAME = 'FindParameterizationsInTree'

    @Resource
    UploadQueueView uploadQueueView

    UploadQueueViewFindParameterizationsInTreeAction() {
        super(ACTION_NAME)
    }

    @Override
    protected List<Parameterization> getItems() {
        uploadQueueView.selectedUploads.simulation.parameterization
    }

    @Override
    boolean isEnabled() {
        uploadQueueView.selectedUploads.size() > 0
    }
}
