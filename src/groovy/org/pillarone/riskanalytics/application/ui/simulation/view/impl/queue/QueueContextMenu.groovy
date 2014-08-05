package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCPopupMenu
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.util.EnabledCheckingMenuItem
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.action.CancelSimulationAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.action.SimulationQueueViewFindParameterizationsInTreeAction
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class QueueContextMenu extends ULCPopupMenu {

    @Resource
    CancelSimulationAction cancelSimulationAction

    @Resource
    SimulationQueueViewFindParameterizationsInTreeAction simulationQueueViewFindParameterizationsInTreeAction
    private List<EnabledCheckingMenuItem> menuItems = []

    @PostConstruct
    void initialize() {
        addItem(cancelSimulationAction)
        addItem(simulationQueueViewFindParameterizationsInTreeAction)
    }

    private void addItem(IAction action) {
        EnabledCheckingMenuItem menuItem = new EnabledCheckingMenuItem(action)
        menuItems << menuItem
        add(menuItem)
    }

    void updateEnablingState() {
        menuItems.each { EnabledCheckingMenuItem menuItem -> menuItem.updateEnablingState() }
    }
}
