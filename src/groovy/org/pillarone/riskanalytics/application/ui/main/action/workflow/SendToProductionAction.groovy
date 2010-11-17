package org.pillarone.riskanalytics.application.ui.main.action.workflow

import org.pillarone.riskanalytics.core.workflow.Status

import org.pillarone.riskanalytics.core.user.UserManagement

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel

class SendToProductionAction extends AbstractWorkflowAction {

    public SendToProductionAction(ULCTableTree tree, P1RATModel model) {
        super("SendToProduction", tree, model);
    }

    Status toStatus() {
        Status.IN_PRODUCTION
    }

    protected String requiredRole() {
        UserManagement.REVIEWER_ROLE
    }

}
