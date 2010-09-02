package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.comment.view.CommentListener

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class InsertCommentAction extends AbstractCommentAction {

    public InsertCommentAction(ULCTableTree tree, int periodIndex) {
        super(tree, periodIndex, "InsertComment");
    }

    void executeAction(String path, int periodIndex, String displayPath) {
        commentListeners.each {CommentListener commentListener ->
            commentListener.addNewCommentView(path, periodIndex, displayPath)
        }
    }


}
