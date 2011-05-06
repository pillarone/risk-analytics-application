package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCTabbedPane
import org.pillarone.riskanalytics.application.ui.main.view.item.IUIItem


class TabbedPaneManager {

    private ULCTabbedPane tabbedPane

    //keep a map of open items to avoid to compare titles to find the tabs

    TabbedPaneManager(ULCTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane
    }

    /**
     * Creates a new tab for the given item
     * The content of a card is currently a TabbedPane.
     * A TabbedPaneManager needs to be created here as well.
     * @param model
     */
    void addTab(IUIItem model) {
        //use IUIItem.createDetailView() to create the content
    }

    /**
     * Opens and selects the card for the given model
     * @param model the model to open
     */
    void selectTab(IUIItem model) {

    }
}
