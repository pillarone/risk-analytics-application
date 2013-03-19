package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel


class SaveAllAction extends ResourceBasedAction {
    RiskAnalyticsMainModel model

    public SaveAllAction(RiskAnalyticsMainModel model) {
        super("SaveAll")
        this.model = model
    }


    public void doActionPerformed(ActionEvent event) {
        ExceptionSafe.protect {
            model.saveAllOpenItems()
        }
    }
}