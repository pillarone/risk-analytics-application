package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe


class SaveAllAction extends ResourceBasedAction {
    P1RATModel model

    public SaveAllAction(P1RATModel model) {
        super("SaveAll")
        this.model = model
    }


    public void doActionPerformed(ActionEvent event) {
        ExceptionSafe.protect {
            model.saveAllOpenItems()
        }
    }
}