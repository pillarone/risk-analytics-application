package org.pillarone.riskanalytics.application.ui.main.action.workflow

import org.pillarone.riskanalytics.core.workflow.Status

import org.pillarone.riskanalytics.core.user.UserManagement

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel

class SendToReviewAction extends AbstractWorkflowAction {

    public SendToReviewAction(ULCTableTree tree, P1RATModel model) {
        super("SendToReview", tree, model);
    }

    Status toStatus() {
        Status.IN_REVIEW
    }

    protected String requiredRole() {
        UserManagement.USER_ROLE
    }

}
