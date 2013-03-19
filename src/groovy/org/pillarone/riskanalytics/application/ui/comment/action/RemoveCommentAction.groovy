package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RemoveCommentAction extends ResourceBasedAction {
    AbstractCommentableItemModel model
    Comment comment
    Closure enablingClosure


    public RemoveCommentAction(AbstractCommentableItemModel model, Comment comment) {
        super("RemoveCommentAction");
        this.model = model;
        this.comment = comment
    }

    void doActionPerformed(ActionEvent event) {
        if (enablingClosure.call()) {
            model.removeComment comment
            saveComments(model.item)
        } else {
            setEnabled(false)
        }
    }

    protected void saveComments(Simulation simulation) {
        simulation.save()
    }

    protected void saveComments(def item) {
    }

    @Override
    boolean isEnabled() {
        return super.isEnabled() && enablingClosure.call()
    }
}
