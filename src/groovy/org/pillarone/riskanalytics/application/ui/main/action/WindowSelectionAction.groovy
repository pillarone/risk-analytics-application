package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.application.ui.main.view.CardPaneManager
import org.pillarone.riskanalytics.core.model.Model

class WindowSelectionAction extends ExceptionSafeAction {

    private final Model model
    private CardPaneManager cardPaneManager

    WindowSelectionAction(Model model, CardPaneManager cardPaneManager) {
        super(getMenuName(model));
        this.model = model
        this.cardPaneManager = cardPaneManager
    }

    public void doActionPerformed(ActionEvent event) {
        cardPaneManager.selectCard(model)
        cardPaneManager.selectCurrentItemFromTab(model)
        trace("Select model ${model?.name}")
    }

    static String getMenuName(Model model) {
        return model ? model.name : "Batches"
    }

}