package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.comment.view.CommentListener
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class EditCommentAction extends AbstractCommentAction {
    Comment comment
    Closure enablingClosure


    public EditCommentAction(Comment comment) {
        super("EditCommentAction");
        this.comment = comment
    }

    void doActionPerformed(ActionEvent event) {
        if (enablingClosure.call()) {
            commentListeners.each {CommentListener commentListener ->
                commentListener.editCommentView(comment)
            }
        } else {
            setEnabled(false)
        }
    }

    void executeAction(String path, int periodIndex, String displayPath) {

    }

    @Override
    boolean isEnabled() {
        return super.isEnabled() && enablingClosure.call()
    }


}
