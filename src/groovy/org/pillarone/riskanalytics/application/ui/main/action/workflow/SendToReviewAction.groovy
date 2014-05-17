package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.workflow.Status

class SendToReviewAction extends AbstractWorkflowAction {

    SendToReviewAction(ULCTableTree tree) {
        super("SendToReview", tree);
    }

    Status toStatus() {
        Status.IN_REVIEW
    }

    @Override
    protected List allowedRoles() {
        return [UserManagement.USER_ROLE]
    }


}
