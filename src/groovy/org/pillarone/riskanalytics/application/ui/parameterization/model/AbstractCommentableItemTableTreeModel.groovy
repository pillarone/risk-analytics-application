package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import org.pillarone.riskanalytics.application.ui.comment.view.ChangedCommentListener
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractCommentableItemTableTreeModel extends AbstractTableTreeModel {

    List<Comment> commentsToBeDeleted = []
    List<ChangedCommentListener> changedCommentListeners = []

    void addChangedCommentListener(ChangedCommentListener listener) {
        changedCommentListeners << listener
    }

    void removeChangedCommentListener(ChangedCommentListener listener) {
        changedCommentListeners.remove(listener)
    }

    void changedComments() {
        changedCommentListeners.each {ChangedCommentListener listener ->
            listener.updateCommentVisualization()
        }
    }

    boolean commentIsVisible(Comment comment) {
        return !commentsToBeDeleted.contains(comment)
    }


}
