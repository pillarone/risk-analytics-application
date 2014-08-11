package org.pillarone.riskanalytics.application.ui.upload.queue.view

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCPopupMenu
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.upload.queue.view.action.CancelUploadAction
import org.pillarone.riskanalytics.application.ui.upload.queue.view.action.UploadQueueViewFindParameterizationsInTreeAction
import org.pillarone.riskanalytics.application.ui.util.EnabledCheckingMenuItem
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class UploadQueueContextMenu extends ULCPopupMenu {

    @Resource
    CancelUploadAction cancelUploadAction

    @Resource
    UploadQueueViewFindParameterizationsInTreeAction uploadQueueViewFindParameterizationsInTreeAction
    private List<EnabledCheckingMenuItem> menuItems = []

    @PostConstruct
    void initialize() {
        addItem(cancelUploadAction)
        addItem(uploadQueueViewFindParameterizationsInTreeAction)
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
