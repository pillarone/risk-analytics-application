package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ShowCommentsView {
    private ULCBoxPane content;
    private ULCBoxPane container;
    private CommentAndErrorView commentAndErrorView
    private ParameterViewModel model;

    public ShowCommentsView(CommentAndErrorView commentAndErrorView) {
        this.commentAndErrorView = commentAndErrorView
        this.model = commentAndErrorView.model
        content = new ULCBoxPane();
        container = new ULCBoxPane(1, 0);
        container.setBackground(Color.white);

        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(container));
        addComments(commentAndErrorView.model.item.comments)
    }

    public void addComment(Comment comment) {
        CommentPane commentPane = new CommentPane(comment)
        commentPane.addCommentListener commentAndErrorView
        container.add(ULCBoxPane.BOX_EXPAND_TOP, commentPane.content)
    }

    public void addComments(Collection<Comment> comments) {
        for (Comment comment: comments) {
            addComment(comment);
        }
        container.add(ULCBoxPane.BOX_EXPAND_EXPAND, ULCFiller.createVerticalGlue());
    }

    public void clear() {
        container.removeAll();
    }

}
