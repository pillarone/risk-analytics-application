package org.pillarone.riskanalytics.application.ui.upload.finished.action
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.batch.action.AbstractSelectItemsInTreeAction
import org.pillarone.riskanalytics.application.ui.upload.finished.view.FinishedUploadsView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedUploadsFindParameterizationsInTreeAction extends AbstractSelectItemsInTreeAction<Parameterization> {
    static final String ACTION_NAME = 'FindParameterizationsInTree'

    @Resource
    FinishedUploadsView finishedUploadsView

    FinishedUploadsFindParameterizationsInTreeAction() {
        super(ACTION_NAME)
    }

    @Override
    protected List<Parameterization> getItems() {
        finishedUploadsView.selectedSimulations.simulation.parameterization
    }

    @Override
    boolean isEnabled() {
        finishedUploadsView.selectedSimulations.size() > 0
    }
}
