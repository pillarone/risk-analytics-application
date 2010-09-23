package org.pillarone.riskanalytics.application.ui.comment.view

import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public interface CommentListener {

    void addNewCommentView(String path, int periodIndex)

    void addNewIssueView(String path, int periodIndex)

    void editCommentView(Comment comment)

    void showCommentsView(String path, int periodIndex)

    void showErrorsView()

}

interface NavigationListener {

    void commentsSelected()
}
