package org.pillarone.riskanalytics.application.ui.main.action

import org.pillarone.riskanalytics.core.simulation.item.Parameterization

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentsSwitchAction extends AbstractAction {
    boolean comment
    P1RATModel model

    public CommentsSwitchAction(model, text, comment) {
        super(text);
        this.model = model
        this.comment = comment;
    }

    public void actionPerformed(ActionEvent event) {
        def currenItem = model.currentItem
        if (currenItem instanceof Parameterization) {
            ParameterViewModel parameterViewModel = model.getParameterViewModel(currenItem)
            parameterViewModel.navigationSelected(comment)
        }

    }

}
