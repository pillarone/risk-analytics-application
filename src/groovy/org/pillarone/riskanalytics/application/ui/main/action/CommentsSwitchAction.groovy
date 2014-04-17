package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationResultUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentsSwitchAction extends AbstractAction {
    boolean comment = false
    private final RiskAnalyticsMainModel model

    CommentsSwitchAction(RiskAnalyticsMainModel model, String text) {
        super(text);
        this.model = model
    }

    void actionPerformed(ActionEvent event) {
        if (enabled) {
            AbstractCommentableItemModel abstractModellingModel = model.getViewModel(model.currentItem) as AbstractCommentableItemModel
            abstractModellingModel.navigationSelected()
        }
    }

    @Override
    boolean isEnabled() {
        return (model.currentItem instanceof ParameterizationUIItem) || (model.currentItem instanceof SimulationResultUIItem)
    }
}
