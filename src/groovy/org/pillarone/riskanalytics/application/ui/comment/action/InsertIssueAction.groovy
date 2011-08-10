package org.pillarone.riskanalytics.application.ui.comment.action

import org.pillarone.riskanalytics.application.ui.comment.view.CommentListener
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.application.UserContext


class InsertIssueAction extends AbstractCommentAction {

    public InsertIssueAction(ULCTableTree tree, int periodIndex) {
        super(tree, periodIndex, "InsertIssue");
    }

    void executeAction(String path, int periodIndex, String displayPath) {
        for (CommentListener commentListener in commentListeners) {
            commentListener.addNewIssueView(path, periodIndex)
        }
    }

    boolean isEnabled() {
        Person user = UserContext.getCurrentUser()
        return user != null && user.getAuthorities()*.authority.contains(UserManagement.REVIEWER_ROLE)
    }


}
