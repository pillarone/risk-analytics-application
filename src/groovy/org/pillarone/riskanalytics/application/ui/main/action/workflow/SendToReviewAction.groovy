package org.pillarone.riskanalytics.application.ui.main.action.workflow

import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import com.ulcjava.base.application.ULCTree
import org.pillarone.riskanalytics.core.user.UserManagement


class SendToReviewAction extends AbstractWorkflowAction {

    public SendToReviewAction(ULCTree tree, P1RATModel model) {
        super("SendToReview", tree, model);
    }

    Status toStatus() {
        Status.IN_REVIEW
    }

    protected String requiredRole() {
        UserManagement.USER_ROLE
    }

}
