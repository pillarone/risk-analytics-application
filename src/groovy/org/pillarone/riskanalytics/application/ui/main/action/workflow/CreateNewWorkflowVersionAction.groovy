package org.pillarone.riskanalytics.application.ui.main.action.workflow

import org.pillarone.riskanalytics.core.workflow.Status
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel


class CreateNewWorkflowVersionAction extends AbstractWorkflowAction {

    CreateNewWorkflowVersionAction(ULCTableTree tree, P1RATModel model) {
        super("NewMajorVersion", tree, model)
    }

    CreateNewWorkflowVersionAction(String name, ULCTableTree tree, P1RATModel model) {
        super(name, tree, model)
    }

    @Override
    Status toStatus() {
        return Status.DATA_ENTRY
    }


}
