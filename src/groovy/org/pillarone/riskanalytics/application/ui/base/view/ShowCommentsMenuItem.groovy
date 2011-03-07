package org.pillarone.riskanalytics.application.ui.base.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.comment.action.ShowCommentsAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ShowCommentsMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    ParameterViewModel model
    ShowCommentsAction showCommentsAction

    public ShowCommentsMenuItem(ShowCommentsAction showCommentsAction, ParameterViewModel model) {
        super(showCommentsAction)
        this.model = model
        this.showCommentsAction = showCommentsAction
    }


    void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        setEnabled(model.isNotEmpty(showCommentsAction.path))
    }


}
