package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel


class SaveAction extends ResourceBasedAction {
    P1RATModel model

    public SaveAction(P1RATModel model) {
        super("Save")
        this.model = model
    }


    public void doActionPerformed(ActionEvent event) {
        model.save()
    }
}
