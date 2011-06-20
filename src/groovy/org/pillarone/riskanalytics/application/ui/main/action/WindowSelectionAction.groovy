package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainView
import org.pillarone.riskanalytics.core.model.Model

class WindowSelectionAction extends ExceptionSafeAction {

    RiskAnalyticsMainView mainView
    Model model

    def WindowSelectionAction(Model model, RiskAnalyticsMainView mainView) {
        super(getMenuName(model));
        this.mainView = mainView
        this.model = model
    }

    public void doActionPerformed(ActionEvent event) {
        mainView.cardPaneManager.selectCard(model)
        mainView.cardPaneManager.selectCurrentItemFromTab(model, mainView.cardPaneManager.getSelectedCard(), mainView.mainModel)
        mainView.setWindowTitle(getMenuName(model))
    }

    static String getMenuName(Model model) {
        return model ? model.name : "Batches"
    }

}