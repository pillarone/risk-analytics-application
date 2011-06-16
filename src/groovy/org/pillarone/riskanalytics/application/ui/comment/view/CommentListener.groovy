package org.pillarone.riskanalytics.application.ui.comment.view

import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public interface CommentListener {

    void addNewCommentView(String path, int periodIndex)

    void addNewFunctionCommentView(List functionsMap)

    void addNewIssueView(String path, int periodIndex)

    void editCommentView(Comment comment)

    void showCommentsView(String path, int periodIndex)

    void showErrorsView()

    void showErrorAndCommentsView()

}

interface NavigationListener {

    void showHiddenComments()

    void showComments()

    void selectTab(int index)
}
