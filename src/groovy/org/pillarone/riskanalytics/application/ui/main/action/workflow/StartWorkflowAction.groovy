package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.workflow.Status

class StartWorkflowAction extends AbstractWorkflowAction {

    public StartWorkflowAction(String name, ULCTableTree tree, RiskAnalyticsMainModel model) {
        super(name, tree, model);
    }

    public StartWorkflowAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("StartWorkflow", tree, model);
    }

    void doActionPerformed(ActionEvent event) {
        DealLinkDialog dialog = new DealLinkDialog(UlcUtilities.getWindowAncestor(tree))
        Parameterization parameterization = getSelectedItem()
        Closure okAction = {
            ExceptionSafe.protect {
                if (!parameterization.isLoaded()) {
                    parameterization.load()
                }
                parameterization.dealId = dialog.dealSelectionModel.dealId
                //ART-392: valuation date functionality currently disabled
                //                         parameterization.valuationDate = dialog.valuationDatePaneModel.valuationDate
                super.doActionPerformed(event)
            }
        }
        if (parameterization.status == Status.NONE) {
            dialog.okAction = okAction
            dialog.selectDeal parameterization
            dialog.show()
        } else {
            super.doActionPerformed(event)
        }

    }

    @Override
    protected Parameterization changeStatus(Parameterization item, Status toStatus) {
        Parameterization result = super.changeStatus(item, toStatus)
        //original parameterization on which workflow is started should not be linked
        item.dealId = null
        item.valuationDate = null
        return result
    }

    Status toStatus() {
        return Status.DATA_ENTRY
    }

    @Override
    protected List allowedRoles() {
        return [UserManagement.USER_ROLE]
    }


}
