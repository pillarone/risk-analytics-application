package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCCardPane
import org.pillarone.riskanalytics.application.ui.main.view.item.ModelUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.IUIItem


class CardPaneManager {

    private ULCCardPane cardPane
    private Map<IUIItem, TabbedPaneManager> tabbedPaneManagers = [:]

    CardPaneManager(ULCCardPane cardPane) {
        this.cardPane = cardPane
    }

    /**
     * Creates a new card for the given model.
     * The content of a card is currently a TabbedPane.
     * A TabbedPaneManager needs to be created here as well.
     * @param model
     */
    void addCard(IUIItem model) {

    }

    /**
     * Opens and selects the card for the given model
     * @param model the model to open
     */
    void selectCard(IUIItem model) {

    }

    /**
     *  opens an item (can be parameterization, result, batch run, comparison etc)
     *  switching cards may be required, then delegate to TabbedPaneManager
     * @param item
     */
    void openItem(IUIItem item) {

    }
}
