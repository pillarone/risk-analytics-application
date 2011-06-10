package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.comment.view.CommentPane
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.FunctionComment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentUtils {

    public static String getCommentTitle(Comment comment, AbstractCommentableItemModel model) {
        String username = comment.user ? comment.user.username : ""
        StringBuilder sb = new StringBuilder(CommentAndErrorView.getDisplayPath(model, comment.getPath()))
        sb.append((comment.getPeriod() != -1) ? " P" + comment.getPeriod() : " " + UIUtils.getText(CommentAndErrorView.class, "forAllPeriods"))
        if (username != "")
            sb.append(" " + UIUtils.getText(CommentPane.class, "user") + ": " + username)
        sb.append(" " + DateFormatUtils.formatDetailed(comment.lastChange))
        return sb.toString()
    }

    public static String getTagsValue(Comment comment) {
        int size = comment.getTags().size()
        StringBuilder sb = new StringBuilder(UIUtils.getText(CommentPane.class, "Tags") + ":")
        sb.append(comment?.getTags()?.join(","))
        if ((comment instanceof FunctionComment) && comment.function) {
            sb.append("<br>" + UIUtils.getText(CommentAndErrorView.class, "Function") + ": ")
            sb.append(comment.function)
        }
        return sb.toString()
    }
}
