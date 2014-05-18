package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.workflow.Status

@CompileStatic
class SendToProductionAction extends AbstractWorkflowAction {

    private static Log LOG = LogFactory.getLog(SendToProductionAction)

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
}