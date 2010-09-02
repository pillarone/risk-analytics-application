package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DeleteCommentAction extends ResourceBasedAction {
    ULCTableTree tree

    public DeleteCommentAction(ULCTableTree tree) {
        super("DeleteCommentAction");
        this.tree = tree;
    }

    void doActionPerformed(ActionEvent event) {
    }
}
