package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.workflow.Status

class StartWorkflowAction extends AbstractWorkflowAction {

    public StartWorkflowAction(String name, ULCTableTree tree, P1RATModel model) {
        super(name, tree, model);
    }

    public StartWorkflowAction(ULCTableTree tree, P1RATModel model) {
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
                parameterization.valuationDate = dialog.valuationDatePaneModel.valuationDate.toDate()
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

    Status toStatus() {
        return Status.DATA_ENTRY
    }

    protected String requiredRole() {
        UserManagement.USER_ROLE
    }


}
