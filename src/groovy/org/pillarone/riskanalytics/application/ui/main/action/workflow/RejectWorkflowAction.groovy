package org.pillarone.riskanalytics.application.ui.main.action.workflow

import org.pillarone.riskanalytics.core.user.UserManagement

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel

class RejectWorkflowAction extends StartWorkflowAction {

    public RejectWorkflowAction(ULCTableTree tree, P1RATModel model) {
        super("RejectWorkflow", tree, model);
    }

    protected String requiredRole() {
        UserManagement.REVIEWER_ROLE
    }


}
