package org.pillarone.riskanalytics.application.ui.upload.finished.action

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.batch.action.AbstractSelectItemsInTreeAction
import org.pillarone.riskanalytics.application.ui.upload.finished.view.FinishedUploadsView
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component('uploadFindResultsInTreeAction')
class FindResultsInTreeAction extends AbstractSelectItemsInTreeAction<Simulation> {

    static final String ACTION_NAME = 'FindResultsInTree'

    @Resource
    FinishedUploadsView finishedUploadsView

    FindResultsInTreeAction() {
        super(ACTION_NAME)
    }

    @Override
    protected List<Simulation> getItems() {
        finishedUploadsView.selectedSimulations.simulation
    }

    @Override
    boolean isEnabled() {
        finishedUploadsView.selectedSimulations.size() > 0
    }
}
