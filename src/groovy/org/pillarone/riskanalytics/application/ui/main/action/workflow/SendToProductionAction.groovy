package org.pillarone.riskanalytics.application.ui.main.action.workflow

import org.pillarone.riskanalytics.core.workflow.Status
import com.ulcjava.base.application.ULCTree
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel


class SendToProductionAction extends AbstractWorkflowAction {

    public SendToProductionAction(ULCTree tree, P1RATModel model) {
        super("SendToProduction", tree, model);
    }

    Status toStatus() {
        Status.IN_PRODUCTION
    }


}
