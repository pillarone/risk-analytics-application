package org.pillarone.riskanalytics.application.ui.comment.action

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.comment.view.CommentListener
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.core.user.Authority
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.application.UserContext


@CompileStatic
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
        return user  && user.getAuthorities().any {Authority auth -> auth.authority == UserManagement.REVIEWER_ROLE}
    }


}
