package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.comment.view.CommentListener
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class EditCommentAction extends AbstractCommentAction {
    Comment comment


    public EditCommentAction(Comment comment) {
        super("EditCommentAction");
        this.comment = comment
    }

    void doActionPerformed(ActionEvent event) {
        commentListeners.each {CommentListener commentListener ->
            commentListener.editCommentView(comment)
        }
    }

    void executeAction(String path, int periodIndex, String displayPath) {

    }


}
