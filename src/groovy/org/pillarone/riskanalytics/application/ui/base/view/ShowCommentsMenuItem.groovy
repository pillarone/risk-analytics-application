package org.pillarone.riskanalytics.application.ui.base.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.comment.action.ShowCommentsAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ShowCommentsMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    AbstractCommentableItemModel model
    ShowCommentsAction showCommentsAction

    public ShowCommentsMenuItem(ShowCommentsAction showCommentsAction, AbstractCommentableItemModel model) {
        super(showCommentsAction)
        this.model = model
        this.showCommentsAction = showCommentsAction
    }


    void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        setEnabled(model.isNotEmpty(showCommentsAction.path))
    }


}
