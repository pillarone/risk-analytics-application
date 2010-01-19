package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils


class RefreshAction extends ResourceBasedAction {
    P1RATModel model

    public RefreshAction(P1RATModel model) {
        super("Refresh")
        this.model = model
        putValue(IAction.SMALL_ICON, UIUtils.getIcon("refresh-active.png"));
    }


    public void doActionPerformed(ActionEvent event) {
        model.refresh()
    }
}
