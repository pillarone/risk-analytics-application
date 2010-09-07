package org.pillarone.riskanalytics.application.ui.comment.action

import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RemoveCommentAction extends ResourceBasedAction {
    ParameterViewModel model
    Comment comment


    public RemoveCommentAction(ParameterViewModel model, Comment comment) {
        super("RemoveCommentAction");
        this.model = model;
        this.comment = comment
    }

    void doActionPerformed(ActionEvent event) {
        model.removeComment comment
    }
}
