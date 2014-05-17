package org.pillarone.riskanalytics.application.ui.main.action.workflow
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.core.user.UserManagement

class RejectWorkflowAction extends StartWorkflowAction {

    public RejectWorkflowAction(ULCTableTree tree) {
        super("RejectWorkflow", tree);
    }

    @Override
    protected List allowedRoles() {
        return [UserManagement.REVIEWER_ROLE]
    }


}
