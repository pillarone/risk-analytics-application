package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.user.UserManagement

class RejectWorkflowAction extends StartWorkflowAction {

    public RejectWorkflowAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("RejectWorkflow", tree, model);
    }

    @Override
    protected List allowedRoles() {
        return [UserManagement.REVIEWER_ROLE]
    }


}
