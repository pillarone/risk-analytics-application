package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.comment.view.CommentListener

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ShowCommentsAction extends AbstractCommentAction {

    public ShowCommentsAction(ULCTableTree tree, int periodIndex) {
        super(tree, periodIndex, "ShowCommentsAction");
    }

    void executeAction(String path, int periodIndex, String displayPath) {
        commentListeners.each {CommentListener commentListener ->
            commentListener.showCommentsView(path, periodIndex, displayPath)
        }
    }


}
