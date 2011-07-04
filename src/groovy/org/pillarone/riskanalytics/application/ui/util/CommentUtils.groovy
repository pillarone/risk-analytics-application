package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.comment.view.CommentPane
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.FunctionComment
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentUtils {

    public static String getCommentTitle(Comment comment, AbstractCommentableItemModel model) {
        StringBuilder sb = new StringBuilder(CommentAndErrorView.getDisplayPath(model, comment.getPath()))
        addCommentInfo(sb, comment)
        return sb.toString()
    }

    public static String getCommentTitle(Comment comment, Class modelClass) {
        String pathDisplayName = ResultViewUtils.getResultNodePathDisplayName(modelClass, comment.path)
        StringBuilder sb = new StringBuilder(pathDisplayName)
        addCommentInfo(sb, comment)
        return sb.toString()
    }

    static void addCommentInfo(StringBuilder sb, Comment comment) {
        String username = comment.user ? comment.user.username : ""
        sb.append((comment.getPeriod() != -1) ? " P" + comment.getPeriod() : " " + UIUtils.getText(CommentAndErrorView.class, "forAllPeriods"))
        if (username != "")
            sb.append(" " + UIUtils.getText(CommentPane.class, "user") + ": " + username)
        sb.append(" " + DateFormatUtils.formatDetailed(comment.lastChange))
        println sb
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

    public static List duplicateComments(Parameterization parameterization, String oldPath, String newPath) {
        List componentComments = []
        parameterization?.comments?.each {Comment comment ->
            String commentPath = comment.path.substring(comment.path.indexOf(":") + 1)
            if (commentPath?.startsWith(oldPath + ":") || commentPath == oldPath)
                componentComments << comment
        }
        List pathCommentList = []
        componentComments?.each {Comment comment ->
            Comment newComment = comment.clone()
            newComment.path = newComment.path.replace("${oldPath}", "${newPath}")
            parameterization.addComment(newComment)
            //node path doesn't start with model
            String treePath = newComment.path.substring(comment.path.indexOf(":") + 1)
            pathCommentList << ["path": treePath, "comment": newComment]
        }
        return pathCommentList
    }
}
