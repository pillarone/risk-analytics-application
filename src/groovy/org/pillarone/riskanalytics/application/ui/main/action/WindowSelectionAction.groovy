package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCCardPane
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.application.ui.main.view.P1RATMainView


class WindowSelectionAction extends ExceptionSafeAction {

    ULCCardPane cardPane
    P1RATMainView view

    def WindowSelectionAction(String name, P1RATMainView view) {
        super(name);
        this.view = view
    }

    public void doActionPerformed(ActionEvent event) {
        Object actionName = getValue(NAME)
        view.modelPane.selectedName = actionName
        view.selectCurrentItemFromTab(view.modelPane.selectedComponent)
        view.windowTitle = actionName
    }

}