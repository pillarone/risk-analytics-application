package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationResultUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentsSwitchAction extends AbstractAction {
    boolean comment
    RiskAnalyticsMainModel model

    public CommentsSwitchAction(model, text, comment) {
        super(text);
        this.model = model
        this.comment = comment;
    }

    public void actionPerformed(ActionEvent event) {
        if (isEnabled()) {
            AbstractModellingModel abstractModellingModel = model.getViewModel(model.currentItem)
            abstractModellingModel.navigationSelected(comment)
        }

    }

    @Override
    boolean isEnabled() {
        return (model.currentItem instanceof ParameterizationUIItem) || (model.currentItem instanceof SimulationResultUIItem)
    }


}
