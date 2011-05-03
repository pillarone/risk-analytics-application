package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.FunctionComment
import org.springframework.transaction.TransactionStatus

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NewFunctionCommentView extends NewCommentView {
    // list of maps containing path, periodIndex and function values of selected cells
    List functionsList

    public NewFunctionCommentView(CommentAndErrorView commentAndErrorView, List functionsList) {
        super(commentAndErrorView)
        this.functionsList = functionsList
        init()
    }

    protected void attachListeners() {
        addButton.addActionListener([actionPerformed: {ActionEvent evt ->
            SimulationRun.withTransaction {TransactionStatus status ->
                functionsList.each {Map functionMap ->
                    addCommentToItem(functionMap['path'], functionMap['periodIndex'], functionMap['function'])
                }
            }
            commentAndErrorView.closeTab()

        }] as IActionListener)

        cancelButton.addActionListener([actionPerformed: {ActionEvent evt ->
            commentAndErrorView.closeTab()
        }] as IActionListener)

    }

    @Override
    String getContentBorderTitle() {
        return ""
    }

    @Override
    protected void addPostTag(Comment comment) {
        if (!functionsList || functionsList.size() < 2) return
        Tag postTag = Tag.findByName(NewCommentView.SHARED_COMMENTS)
        if (!comment.tags.contains(postTag))
            comment.addTag(postTag)
    }



    protected Comment createComment(String path, int periodIndex, String function = null) {
        return new FunctionComment(path, periodIndex, function)
    }


}
