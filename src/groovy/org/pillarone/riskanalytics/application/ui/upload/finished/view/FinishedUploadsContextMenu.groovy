package org.pillarone.riskanalytics.application.ui.upload.finished.view

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCPopupMenu
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.upload.finished.action.*
import org.pillarone.riskanalytics.application.ui.util.EnabledCheckingMenuItem
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedUploadsContextMenu extends ULCPopupMenu {

    @Resource
    OpenResultsAction uploadOpenResultsAction
    @Resource
    ClearSelectedAction uploadClearSelectedAction
    @Resource
    ClearAllAction uploadClearAllAction
    @Resource
    FinishedUploadsFindParameterizationsInTreeAction finishedUploadsFindParameterizationsInTreeAction
    @Resource
    FindResultsInTreeAction uploadFindResultsInTreeAction

    private List<EnabledCheckingMenuItem> menuItems = []

    @PostConstruct
    void initialize() {
        addItem(uploadOpenResultsAction)
        addItem(uploadClearSelectedAction)
        addItem(uploadClearAllAction)
        addItem(finishedUploadsFindParameterizationsInTreeAction)
        addItem(uploadFindResultsInTreeAction)
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
