package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCCardPane


class RiskAnalyticsMainView {

    private ULCCardPane modelCardPane
    private CardPaneManager cardPaneManager

    RiskAnalyticsMainModel model

    RiskAnalyticsMainView(RiskAnalyticsMainModel model) {
        this.model = model

        initComponents()
    }

    protected initComponents() {
        modelCardPane = new ULCCardPane()
        cardPaneManager = new CardPaneManager(modelCardPane)
    }
}
