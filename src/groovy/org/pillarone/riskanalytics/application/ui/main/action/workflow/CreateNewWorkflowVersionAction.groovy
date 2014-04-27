package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.core.workflow.WorkflowException

class CreateNewWorkflowVersionAction extends AbstractWorkflowAction {

    CreateNewWorkflowVersionAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("NewWorkflowVersion", tree, model)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        Parameterization parameterization = getSelectedItem()
        SortedSet allVersions = new TreeSet(VersionNumber.getExistingVersions(parameterization))
        if (parameterization.versionNumber != allVersions.last()) {
            throw new WorkflowException( parameterization.getNameAndVersion(),
                                         toStatus(),
                                         "Cannot create a new version. A newer version already exists: ${allVersions.last()}"   )
        }
        super.doActionPerformed(event)
    }



    @Override
    Status toStatus() {
        return Status.DATA_ENTRY
    }


}
