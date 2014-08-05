package org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCPopupMenu
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.util.EnabledCheckingMenuItem
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.finished.action.*
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedSimulationsContextMenu extends ULCPopupMenu {

    @Resource
    OpenResultsAction openResultsAction
    @Resource
    ClearSelectedAction clearSelectedAction
    @Resource
    ClearAllAction clearAllAction
    @Resource
    FinishedSimulationsFindParameterizationsInTreeAction finishedSimulationsFindParameterizationsInTreeAction
    @Resource
    FindResultsInTreeAction findResultsInTreeAction

    private List<EnabledCheckingMenuItem> menuItems = []

    @PostConstruct
    void initialize() {
        addItem(openResultsAction)
        addItem(clearSelectedAction)
        addItem(clearAllAction)
        addItem(finishedSimulationsFindParameterizationsInTreeAction)
        addItem(findResultsInTreeAction)
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
