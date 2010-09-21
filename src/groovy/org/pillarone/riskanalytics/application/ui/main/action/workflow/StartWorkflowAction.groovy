package org.pillarone.riskanalytics.application.ui.main.action.workflow

import org.pillarone.riskanalytics.core.workflow.Status
import com.ulcjava.base.application.ULCTree
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel


class StartWorkflowAction extends AbstractWorkflowAction {

    public StartWorkflowAction(String name, ULCTree tree, P1RATModel model) {
        super(name, tree, model);
    }

    public StartWorkflowAction(ULCTree tree, P1RATModel model) {
        super("StartWorkflow", tree, model);
    }

    Status toStatus() {
        return Status.DATA_ENTRY
    }


}
