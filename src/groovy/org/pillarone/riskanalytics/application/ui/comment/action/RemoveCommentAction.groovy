package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RemoveCommentAction extends ResourceBasedAction {
    ParameterViewModel model
    Comment comment
    Closure enablingClosure


    public RemoveCommentAction(ParameterViewModel model, Comment comment) {
        super("RemoveCommentAction");
        this.model = model;
        this.comment = comment
    }

    void doActionPerformed(ActionEvent event) {
        model.removeComment comment
    }

    @Override
    boolean isEnabled() {
        return super.isEnabled() && enablingClosure.call()
    }
}
