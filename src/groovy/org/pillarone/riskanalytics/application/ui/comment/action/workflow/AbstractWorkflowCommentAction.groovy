package org.pillarone.riskanalytics.application.ui.comment.action.workflow

import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.application.ui.comment.view.WorkflowCommentPane
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.workflow.WorkflowComment
import com.ulcjava.base.application.event.ActionEvent


abstract class AbstractWorkflowCommentAction extends ExceptionSafeAction {

    private WorkflowCommentPane commentPane


    public AbstractWorkflowCommentAction(String name, WorkflowCommentPane commentPane) {
        super(name);
        this.commentPane = commentPane;
    }

    abstract void changeStatus(WorkflowComment comment)

    final void doActionPerformed(ActionEvent event) {
        changeStatus(commentPane.comment)
        commentPane.model.item.changed = true
        commentPane.updateUI()
    }


}

class ResolveWorkflowCommentAction extends AbstractWorkflowCommentAction {

    public ResolveWorkflowCommentAction(WorkflowCommentPane commentPane) {
        super("Resolve", commentPane);
    }

    void changeStatus(WorkflowComment comment) {
        comment.resolve()
    }

}

class ReopenWorkflowCommentAction extends AbstractWorkflowCommentAction {

    public ReopenWorkflowCommentAction(WorkflowCommentPane commentPane) {
        super("Reopen", commentPane);
    }

    void changeStatus(WorkflowComment comment) {
        comment.reopen()
    }

}

class CloseWorkflowCommentAction extends AbstractWorkflowCommentAction {

    public CloseWorkflowCommentAction(WorkflowCommentPane commentPane) {
        super("Close", commentPane);
    }

    void changeStatus(WorkflowComment comment) {
        comment.close()
    }

}
