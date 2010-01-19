package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.view.P1RATMainView


/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class ShowUserSettingsAction extends ResourceBasedAction {
    P1RATMainView view

    public ShowUserSettingsAction(P1RATMainView view) {
        super("ShowUserSettings")
        this.view = view
    }



    public void doActionPerformed(ActionEvent event) {
        view.openSettingsViewDialog()
    }
}