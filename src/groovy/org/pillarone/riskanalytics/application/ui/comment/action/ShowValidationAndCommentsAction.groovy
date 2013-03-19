package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.comment.view.CommentListener

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ShowValidationAndCommentsAction extends AbstractCommentAction {

    public ShowValidationAndCommentsAction(ULCTableTree tree) {
        super(tree, -1, "ShowValidationAndCommentsAction");
    }

    void executeAction(String path, int periodIndex, String displayPath) {
        commentListeners.each {CommentListener commentListener ->
            commentListener.showErrorAndCommentsView()
        }
    }
}
