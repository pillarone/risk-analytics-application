package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCCardPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTabbedPane
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.core.model.Model

class CardPaneManager {

    private ULCCardPane cardPane
    private Map<String, TabbedPaneManager> tabbedPaneManagers = [:]
    private RiskAnalyticsMainModel mainModel
    public static final String NO_MODEL = "NO_MODEL"
    Log LOG = LogFactory.getLog(CardPaneManager)

    public CardPaneManager(ULCCardPane cardPane, RiskAnalyticsMainModel mainModel) {
        this.cardPane = cardPane
        this.mainModel = mainModel
    }

    /**
     * Creates a new card for the given model.
     * The content of a card is currently a TabbedPane.
     * A TabbedPaneManager needs to be created here as well.
     * @param model
     */
    void addCard(Model selectedModel) {
        ULCTabbedPane modelCardContent = createDetachableTabbedPane(selectedModel)
        cardPane.addCard(getModelName(selectedModel), modelCardContent)
        Closure closeAction = { event -> closeCard(selectedModel, modelCardContent, modelCardContent.getSelectedIndex()) }

        modelCardContent.registerKeyboardAction([actionPerformed: closeAction] as IActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_IN_FOCUSED_WINDOW)
        selectCard(selectedModel)
        tabbedPaneManagers.put(getModelName(selectedModel), new TabbedPaneManager(modelCardContent))
    }


    void removeCard(Model selectedModel) {
        if (!contains(selectedModel)) return
        cardPane.removeCard(getModelName(selectedModel))
        tabbedPaneManagers.remove(getModelName(selectedModel))
    }

    /**
     * Opens and selects the card for the given model
     * @param model the model to open
     */
    boolean selectCard(Model selectedModel) {
        if (!contains(selectedModel)) return false
        cardPane.selectedName = getModelName(selectedModel)
        return true
    }

    ULCComponent getSelectedCard() {
        return cardPane.getSelectedComponent()
    }

    /**
     *  opens an item (can be parameterization, result, batch run, comparison etc)
     *  switching cards may be required, then delegate to TabbedPaneManager
     * @param item
     */
    void openItem(Model selectedModel, AbstractUIItem item) {
        if (!selectCard(selectedModel)) {
            addCard(selectedModel)
        }
        TabbedPaneManager tabbedPaneManager = tabbedPaneManagers.get(getModelName(selectedModel))
        if (tabbedPaneManager.tabExists(item)) {
            tabbedPaneManager.selectTab(item)
        } else {
            tabbedPaneManager.addTab(item)
        }
        mainModel.setCurrentItem(item)
    }

    public boolean contains(Model selectedModel) {
        return (cardPane.getNames() as List).contains(getModelName(selectedModel))
    }

    private String getModelName(Model selectedModel) {
        return selectedModel ? selectedModel.name : NO_MODEL
    }

    private void closeCard(Model model, ULCTabbedPane modelCardContent, int closingIndex) {
        if (closingIndex == -1) return
        TabbedPaneManager tabbedPaneManager = tabbedPaneManagers.get(getModelName(model))
        AbstractUIItem abstractUIItem = tabbedPaneManager.getAbstractItem(modelCardContent.getComponentAt(closingIndex))
        if (!abstractUIItem) return
        tabbedPaneManager.closeTab(abstractUIItem)

        if (modelCardContent && modelCardContent.getTabCount() == 0)
            removeCard(model)
    }

    public ULCTabbedPane createDetachableTabbedPane(Model selectedModel) {
        ULCTabbedPane tabbedPane = new ULCDetachableTabbedPane(name: "DetachableTabbedPane")
        tabbedPane.addTabListener([tabClosing: {TabEvent event ->
            int closingIndex = event.getTabClosingIndex()
            if (closingIndex < 0) closingIndex = 0
            ULCCloseableTabbedPane modelCardContent = event.getClosableTabbedPane()
            closeCard(selectedModel, modelCardContent, closingIndex)
        }] as ITabListener)
        Closure syncCurrentItem = {e -> selectCurrentItemFromTab(selectedModel, e.source)}
        tabbedPane.selectionChanged = syncCurrentItem
        tabbedPane.focusGained = syncCurrentItem
        return tabbedPane
    }

    public void selectCurrentItemFromTab(Model selectedModel, ULCCloseableTabbedPane modelCardContent) {
        try {
            TabbedPaneManager tabbedPaneManager = tabbedPaneManagers.get(getModelName(selectedModel))
            if (tabbedPaneManager) {
                AbstractUIItem item = tabbedPaneManager.getAbstractItem(modelCardContent.getSelectedComponent())
                //mainModel.currentItem = (item instanceof BatchRun) ? null : item
                mainModel.notifyChangedDetailView(selectedModel, item)
            }
        } catch (Exception ex) {
            LOG.error "Error occured during set a current item ${ex}"
        }
    }


    TabbedPaneManager getTabbedPaneManager(Model selectedModel) {
        return tabbedPaneManagers.get(getModelName(selectedModel))
    }
}
