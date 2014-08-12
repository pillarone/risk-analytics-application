package org.pillarone.riskanalytics.application.ui.upload.finished.action
import com.ulcjava.base.application.event.ActionEvent
import groovy.util.logging.Log
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.upload.finished.view.FinishedUploadsView
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.upload.UploadRuntimeInfo
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

import static org.pillarone.riskanalytics.core.upload.UploadState.DONE


@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component('uploadOpenResultsAction')
@Log
class OpenResultsAction extends ResourceBasedAction {

    @Resource
    FinishedUploadsView finishedUploadsView

    OpenResultsAction() {
        super("OpenResults");
    }

    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            Simulation simulation = finishedUploadsView?.selectedSimulations?.first()?.simulation
            if (simulation) {
                riskAnalyticsEventBus.post(new OpenDetailViewEvent(UIItemFactory.createItem(simulation)))
            } else {
                log.warn("Can't open last finished sim from finishedSimulationView")
            }
        }
    }

    @Override
    boolean isEnabled() {
        List<UploadRuntimeInfo> simulations = finishedUploadsView.selectedSimulations
        simulations.size() == 1 && simulations.first().uploadState == DONE
    }
}
