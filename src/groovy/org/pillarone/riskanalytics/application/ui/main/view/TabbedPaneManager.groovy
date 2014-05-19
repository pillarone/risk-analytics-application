package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.action.SaveAction
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert

class TabbedPaneManager {

    private ULCDetachableTabbedPane tabbedPane
    private DependentFramesManager dependentFramesManager
    private Map<AbstractUIItem, ULCComponent> tabManager = [:]

    //keep a map of open items to avoid to compare titles to find the tabs

    TabbedPaneManager(ULCDetachableTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane
        this.dependentFramesManager = new DependentFramesManager(this.tabbedPane)
    }

    private DetailViewManager getDetailViewManager() {
        Holders.grailsApplication.mainContext.getBean('detailViewManager', DetailViewManager)
    }

    private RiskAnalyticsMainView getRiskAnalyticsMainView() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainView', RiskAnalyticsMainView)
    }

    /**
     * Creates a new tab for the given item
     * The content of a card is currently a TabbedPane.
     * A TabbedPaneManager needs to be created here as well.
     * @param model
     */
    void addTab(AbstractUIItem item) {
        IDetailView detailView = detailViewManager.createDetailViewForItem(item)
        if (item instanceof ModellingUIItem) {
            item.addModellingItemChangeListener(riskAnalyticsMainView)
            item.addModellingItemChangeListener new MarkItemAsUnsavedListener(this, item)
        }
        ULCContainer view = detailView.content
        def wrapped = new ULCScrollPane(view, ULCScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, ULCScrollPane.HORIZONTAL_SCROLLBAR_NEVER)
        tabManager[item] = wrapped
        tabbedPane.addTab(item.createTitle(), item.icon, wrapped)
        int tabIndex = tabbedPane.tabCount - 1
        tabbedPane.selectedIndex = tabIndex
        tabbedPane.setToolTipTextAt(tabIndex, item.toolTip)
    }

    void closeTab(ULCComponent component) {
        AbstractUIItem item = getAbstractItem(component)
        if (item) {
            closeTabForItem(item)
        }
    }

    private void closeTabForItem(AbstractUIItem abstractUIItem) {
        if (abstractUIItem instanceof ModellingUIItem && abstractUIItem.item.changed) {
            ModellingUIItem modellingUIItem = abstractUIItem
            boolean closeTab = true
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tabManager[modellingUIItem]), "itemChanged")
            alert.addWindowListener([windowClosing: { WindowEvent windowEvent ->
                def value = windowEvent.source.value
                if (value.equals(alert.firstButtonLabel)) {
                    SaveAction saveAction = new SaveAction(tabbedPane, modellingUIItem.item)
                    saveAction.doActionPerformed(new ActionEvent(this, "save"))
                } else if (value.equals(alert.thirdButtonLabel)) {
                    closeTab = false

                } else {
                    modellingUIItem.unload()
                }
                if (closeTab) {
                    removeTab(modellingUIItem)
                } else {
                    selectTab(modellingUIItem)
                }
            }] as IWindowListener)
            alert.show()
        } else {
            removeTab(abstractUIItem)
        }
    }

    /**
     * Opens and selects the card for the given model
     * @param model the model to open
     */
    void selectTab(AbstractUIItem item) {
        ULCComponent component = tabManager[item]
        if (tabbedPane.indexOfComponent(component) >= 0) {
            tabbedPane.selectedComponent = component
        } else {
            dependentFramesManager.selectTab(item)
        }
    }

    /**
     * Removes a tab from the Tabbed Pane for the given item
     * @param item
     */
    void removeTab(AbstractUIItem item) {
        ULCComponent component = tabManager[item]
        if (component) {
            if (tabbedPane.indexOfComponent(component) >= 0) {
                tabbedPane.remove(component)
            } else {
                dependentFramesManager.closeTab(item)
            }
            tabManager.remove(item)
        }
        detailViewManager.close(item)
    }

    boolean tabExists(AbstractUIItem abstractUIItem) {
        return tabManager.containsKey(abstractUIItem)
    }

    AbstractUIItem getAbstractItem(ULCComponent component) {
        AbstractUIItem abstractUIItem = null
        tabManager.each { k, v ->
            if (v == component)
                abstractUIItem = k
        }
        return abstractUIItem
    }

    void updateTabbedPaneTitle(AbstractUIItem abstractUIItem) {
        ULCComponent component = tabManager[abstractUIItem]
        if (component) {
            int tabIndex = tabbedPane.indexOfComponent(component)
            if (tabIndex >= 0)
                tabbedPane.setTitleAt(tabIndex, abstractUIItem.createTitle())
            else {
                dependentFramesManager.updateTabbedPaneTitle(abstractUIItem)
            }
        }
    }
}
