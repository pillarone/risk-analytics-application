package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.parameter.comment.workflow.IssueStatus
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.workflow.WorkflowComment
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.workflow.Status

class SendToProductionAction extends AbstractWorkflowAction {

    public SendToProductionAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("SendToProduction", tree, model);
    }

    Status toStatus() {
        Status.IN_PRODUCTION
    }


    @Override
    protected List allowedRoles() {
        return [UserManagement.REVIEWER_ROLE]
    }



    protected boolean isActionEnabled() {
        def item = getSelectedItem()
        if (item == null || !(item instanceof Parameterization)) return super.isActionEnabled()
        if (!item.isLoaded()) {
            item.load()
        }
        boolean issueNotClosed = item.comments?.findAll {(it instanceof WorkflowComment) && it.status != IssueStatus.CLOSED}?.size() > 0
        return super.isActionEnabled() && !issueNotClosed;
    }


}
