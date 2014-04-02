package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.action.CancelSimulationAction
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class QueueContextMenu extends ULCPopupMenu {

    @Resource
    CancelSimulationAction cancelSimulationAction

    @PostConstruct
    void initialize() {
        name = "batchesNodePopUpMenu"
        add(new ULCMenuItem(cancelSimulationAction))
    }
}
