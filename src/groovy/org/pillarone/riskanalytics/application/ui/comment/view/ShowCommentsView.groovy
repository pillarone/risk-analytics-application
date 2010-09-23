package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.workflow.WorkflowComment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ShowCommentsView implements ChangedCommentListener {
    private ULCBoxPane content;
    private ULCBoxPane container;
    private CommentAndErrorView commentAndErrorView
    private ParameterViewModel model;
    String path

    public ShowCommentsView(CommentAndErrorView commentAndErrorView, String path) {
        this.commentAndErrorView = commentAndErrorView
        this.model = commentAndErrorView.model
        this.path = path
        content = new ULCBoxPane(name: "Comments");
        container = new ULCBoxPane(1, 0);
        container.setBackground(Color.white);

        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(container));
        def allComments = getComments()
        addComments(allComments)
        model.commentsChanged(allComments)
    }

    public void addComment(Comment comment) {
        CommentPane commentPane = createCommentPane(comment)
        commentPane.addCommentListener commentAndErrorView
        container.add(ULCBoxPane.BOX_EXPAND_TOP, commentPane.content)
    }

    protected CommentPane createCommentPane(Comment comment) {
        return new CommentPane(model, comment)
    }

    protected CommentPane createCommentPane(WorkflowComment comment) {
        return new WorkflowCommentPane(model, comment)
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

    void updateCommentVisualization() {
        clear()
        addComments(getComments())
    }

    private List<Comment> getComments() {
        def all = model.item.comments.findAll {!it.deleted}//.sort{it.path}
        return path ? all.findAll {it.path == path} : all
    }


}

interface ChangedCommentListener {
    void updateCommentVisualization()
}