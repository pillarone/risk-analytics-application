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
import grails.util.Holders
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.eventbus.RiskAnalyticsEventBus
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.ChangeDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.core.model.Model
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class CardPaneManager {
    private static final Log LOG = LogFactory.getLog(CardPaneManager)

    ULCCardPane cardPane = new ULCCardPane()
    private Map<String, TabbedPaneManager> tabbedPaneManagers = [:]

    static final String NO_MODEL = "NO_MODEL"

    /**
     * Creates a new card for the given model.
     * The content of a card is currently a TabbedPane.
     * A TabbedPaneManager needs to be created here as well.
     * @param model
     */
    void addCard(Model selectedModel) {
        ULCDetachableTabbedPane modelCardContent = createDetachableTabbedPane(selectedModel)
        cardPane.addCard(getModelName(selectedModel), modelCardContent)
        Closure closeAction = { event -> closeCard(selectedModel, modelCardContent, modelCardContent.selectedIndex) }

        modelCardContent.registerKeyboardAction([actionPerformed: closeAction] as IActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_IN_FOCUSED_WINDOW)
        selectCard(selectedModel)
        tabbedPaneManagers[getModelName(selectedModel)] = new TabbedPaneManager(modelCardContent)
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
        return cardPane.selectedComponent
    }

    /**
     *  opens an item (can be parameterization, result, batch run, comparison etc)
     *  switching cards may be required, then delegate to TabbedPaneManager
     * @param item
     */
    void openItem(AbstractUIItem item) {
        Model selectedModel = item.model
        if (!selectCard(selectedModel)) {
            addCard(selectedModel)
        }
        TabbedPaneManager tabbedPaneManager = tabbedPaneManagers[getModelName(selectedModel)]
        if (tabbedPaneManager.tabExists(item)) {
            tabbedPaneManager.selectTab(item)
        } else {
            tabbedPaneManager.addTab(item)
        }
    }

    public boolean contains(Model selectedModel) {
        return (cardPane.names as List).contains(getModelName(selectedModel))
    }

    private String getModelName(Model selectedModel) {
        return selectedModel ? selectedModel.name : NO_MODEL
    }

    private void closeCard(Model model, ULCTabbedPane modelCardContent, int closingIndex) {
        if (closingIndex == -1) return
        TabbedPaneManager tabbedPaneManager = tabbedPaneManagers[getModelName(model)]
        tabbedPaneManager.closeTab(modelCardContent.getComponentAt(closingIndex))
        if (modelCardContent && modelCardContent.tabCount == 0) {
            removeCard(model)
        }
    }

    public ULCDetachableTabbedPane createDetachableTabbedPane(Model selectedModel) {
        ULCTabbedPane tabbedPane = new ULCDetachableTabbedPane(name: "DetachableTabbedPane")
        tabbedPane.addTabListener([tabClosing: { TabEvent event ->
            int closingIndex = event.tabClosingIndex
            if (closingIndex < 0) closingIndex = 0
            ULCCloseableTabbedPane modelCardContent = event.closableTabbedPane
            closeCard(selectedModel, modelCardContent, closingIndex)
        }] as ITabListener)
        Closure syncCurrentItem = { e -> selectCurrentItemFromTab(selectedModel, e.source) }
        tabbedPane.selectionChanged = syncCurrentItem
        tabbedPane.focusGained = syncCurrentItem
        return tabbedPane
    }

    public void selectCurrentItemFromTab(Model selectedModel, ULCCloseableTabbedPane modelCardContent) {
        TabbedPaneManager tabbedPaneManager = tabbedPaneManagers[getModelName(selectedModel)]
        if (tabbedPaneManager) {
            AbstractUIItem item = tabbedPaneManager.getAbstractItem(modelCardContent.selectedComponent)
            riskAnalyticsEventBus.post(new ChangeDetailViewEvent(item))
        }
    }

    RiskAnalyticsEventBus getRiskAnalyticsEventBus() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsEventBus', RiskAnalyticsEventBus)
    }


    TabbedPaneManager getTabbedPaneManager(Model selectedModel) {
        return tabbedPaneManagers[getModelName(selectedModel)]
    }

}
