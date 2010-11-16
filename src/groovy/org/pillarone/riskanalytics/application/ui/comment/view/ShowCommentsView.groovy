package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
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
    List<Comment> comments

    public ShowCommentsView(CommentAndErrorView commentAndErrorView, String path) {
        this.commentAndErrorView = commentAndErrorView
        this.model = commentAndErrorView.model
        this.path = path
        content = new ULCBoxPane(name: "Comments");
        container = new ULCBoxPane(1, 0);
        container.setBackground(Color.white);

        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(container));
        this.comments = []

    }

    public void addAllComments() {
        comments.clear()
        comments = getAllComments()
        order("lastChange", "desc")
        addComments(comments)
        model.commentsChanged(allComments)
    }

    public void addComment(Comment comment, String searchText = null) {
        CommentPane commentPane = createCommentPane(comment, searchText)
        commentPane.addCommentListener commentAndErrorView
        container.add(ULCBoxPane.BOX_EXPAND_TOP, commentPane.content)
    }
    
    protected CommentPane createCommentPane(Comment comment, String searchText) {
        return new CommentPane(model, comment, searchText)
    }

    protected CommentPane createCommentPane(WorkflowComment comment, String searchText) {
        return new WorkflowCommentPane(model, comment, searchText)
    }

    public void addComments(Collection<Comment> comments, String searchText = null) {
        clear()
        if (comments && !comments.isEmpty()) {
            for (Comment comment: comments) {
                addComment(comment, searchText);
            }
        } else {
            ULCLabel label = new ULCLabel(UIUtils.getText(this.class, "noComment"))
            label.name = "noComment"
            ULCBoxPane around = UIUtils.spaceAround(label, 2, 10, 0, 0)
            around.setBackground(Color.white)
            container.add(ULCBoxPane.BOX_LEFT_TOP, around)
        }
        container.add(ULCBoxPane.BOX_EXPAND_EXPAND, ULCFiller.createVerticalGlue());
    }

    public void clear() {
        container.removeAll();
    }

    void updateCommentVisualization() {
        clear()
        addComments(getAllComments())
    }

    void order(String orderBy, String order) {
        def comparator = { x, y -> if ("asc" == order) x.properties[orderBy] <=> y.properties[orderBy] else y.properties[orderBy] <=> x.properties[orderBy] } as Comparator
        if (!comments || comments.size() == 0)
            comments = getAllComments()
        comments.sort(comparator)
        clear()
        addComments(comments)
    }

    private List<Comment> getAllComments() {
        def all = model.item.comments.findAll {!it.deleted && model.commentIsVisible(it)}
        return path ? all.findAll {it.path == path} : all
    }

    public void setVisible(boolean visibility) {
        container.setVisible visibility
    }

    public boolean isVisible() {
        return container.isVisible()
    }

}

interface ChangedCommentListener {
    void updateCommentVisualization()
}