package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCTableTree
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.comment.view.CommentListener

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class ShowErrorsAction extends AbstractCommentAction {

    public ShowErrorsAction(ULCTableTree tree) {
        super(tree, -1, "ShowErrorsAction")
    }

    void executeAction(String path, int periodIndex, String displayPath) {
        commentListeners.each {CommentListener commentListener ->
            commentListener.showErrorsView()
        }
    }
}
