package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.parameter.comment.workflow.IssueStatus
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.workflow.WorkflowComment
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.workflow.Status

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
