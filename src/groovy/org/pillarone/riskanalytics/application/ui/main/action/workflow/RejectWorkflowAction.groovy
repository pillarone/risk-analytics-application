package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTree
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.user.UserManagement


class RejectWorkflowAction extends StartWorkflowAction {

    public RejectWorkflowAction(ULCTree tree, P1RATModel model) {
        super("RejectWorkflow", tree, model);
    }

    protected String requiredRole() {
        UserManagement.REVIEWER_ROLE
    }


}
