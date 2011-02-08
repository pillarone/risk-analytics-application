package org.pillarone.riskanalytics.application.ui.comment.view

import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.workflow.WorkflowComment
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.ULCBoxPane
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.ULCScrollPane


class NewIssueView extends NewCommentView {

    public NewIssueView(CommentAndErrorView commentAndErrorView) {
        super(commentAndErrorView);
    }

    public NewIssueView(CommentAndErrorView commentAndErrorView, String path, int periodIndex) {
        super(commentAndErrorView, path, periodIndex);
    }

    protected Comment createComment(String path, int periodIndex) {
        return new WorkflowComment(path, periodIndex)
    }

    protected void layoutComponents() {
        content.add(ULCBoxPane.BOX_LEFT_TOP, UIUtils.spaceAround(new ULCScrollPane(commentTextArea), 5, 5, 0, 0))
        content.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller());
        content.add(ULCBoxPane.BOX_LEFT_TOP, getButtonsPane())
        content.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller());
        content.add(3, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller());
    }


}
