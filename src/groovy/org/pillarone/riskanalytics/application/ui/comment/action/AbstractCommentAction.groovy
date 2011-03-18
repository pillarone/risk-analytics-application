package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.comment.view.CommentListener

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractCommentAction extends ResourceBasedAction {
    ULCTableTree tree
    List<CommentListener> commentListeners
    int periodIndex

    public AbstractCommentAction(ULCTableTree tree, int periodIndex, String actionName) {
        super(actionName);
        this.tree = tree
        commentListeners = []
        this.periodIndex = periodIndex
    }

    public AbstractCommentAction(String actionName) {
        super(actionName);
        commentListeners = []
    }

    void doActionPerformed(ActionEvent event) {
        String path = tree?.selectedPath?.lastPathComponent?.path
        String displayPath = tree?.selectedPath?.lastPathComponent?.getDisplayPath()
        if (path) {
            executeAction(path, periodIndex, displayPath)
        }
    }

    String getPath() {
        return tree?.selectedPath?.lastPathComponent?.path
    }



    void addCommentListener(CommentListener listener) {
        commentListeners << listener
    }

    abstract void executeAction(String path, int periodIndex, String displayPath)

    ;
}
