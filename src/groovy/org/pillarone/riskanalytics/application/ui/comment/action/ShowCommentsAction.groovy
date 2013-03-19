package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.comment.view.CommentListener

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ShowCommentsAction extends AbstractCommentAction {
    boolean all

    public ShowCommentsAction(ULCTableTree tree, int periodIndex, boolean all) {
        super(tree, periodIndex, all ? "ShowAllCommentsAction" : "ShowCommentsAction");
        this.all = all
    }

    void executeAction(String path, int periodIndex, String displayPath) {
        path = !all ? getPath() : null
        commentListeners.each {CommentListener commentListener ->
            commentListener.showCommentsView(path, periodIndex)
        }
    }


}
