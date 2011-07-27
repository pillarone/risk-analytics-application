package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainView
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.application.ui.main.view.CardPaneManager
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane

class WindowSelectionAction extends ExceptionSafeAction {

    RiskAnalyticsMainView mainView
    Model model
    private CardPaneManager cardPaneManager

    def WindowSelectionAction(Model model, CardPaneManager cardPaneManager) {
        super(getMenuName(model));
        this.model = model
        this.cardPaneManager = cardPaneManager
    }

    public void doActionPerformed(ActionEvent event) {
        cardPaneManager.selectCard(model)
        cardPaneManager.selectCurrentItemFromTab(model, (ULCCloseableTabbedPane)cardPaneManager.getSelectedCard())
    }

    static String getMenuName(Model model) {
        return model ? model.name : "Batches"
    }

}