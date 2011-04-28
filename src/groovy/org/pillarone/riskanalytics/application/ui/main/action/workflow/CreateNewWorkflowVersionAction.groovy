package org.pillarone.riskanalytics.application.ui.main.action.workflow

import org.pillarone.riskanalytics.core.workflow.Status
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.workflow.WorkflowException


class CreateNewWorkflowVersionAction extends AbstractWorkflowAction {

    CreateNewWorkflowVersionAction(ULCTableTree tree, P1RATModel model) {
        super("NewMajorVersion", tree, model)
    }

    CreateNewWorkflowVersionAction(String name, ULCTableTree tree, P1RATModel model) {
        super(name, tree, model)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        Parameterization parameterization = getSelectedItem()
        SortedSet allVersions = new TreeSet(VersionNumber.getExistingVersions(parameterization))
        if(parameterization.versionNumber != allVersions.last()) {
            throw new WorkflowException(parameterization.name, toStatus(), "Cannot create a new version. A newer version already exists: ${allVersions.last()}")
        }
        super.doActionPerformed(event)
    }



    @Override
    Status toStatus() {
        return Status.DATA_ENTRY
    }


}
