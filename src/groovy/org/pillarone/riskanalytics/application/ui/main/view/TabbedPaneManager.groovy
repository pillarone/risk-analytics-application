package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.ULCTabbedPane
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem

class TabbedPaneManager {

    private ULCTabbedPane tabbedPane
    private Map<AbstractUIItem, ULCComponent> tabManager = [:]

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
    public void addTab(AbstractUIItem item) {
        //use IUIItem.createDetailView() to create the content
        //todo fja icon and toolTip
        ULCContainer view = item.createDetailView()
        tabbedPane.addTab(item.createTitle(), item.getIcon(), view)
        int tabIndex = tabbedPane.tabCount - 1
        tabbedPane.selectedIndex = tabIndex
        tabManager.put(item, view)
        item.addModellingItemChangeListener new MarkItemAsUnsavedListener(this,tabbedPane, item)
        tabbedPane.setToolTipTextAt(tabIndex, item.getToolTip())
    }

    /**
     * Opens and selects the card for the given model
     * @param model the model to open
     */
    public void selectTab(AbstractUIItem item) {
        ULCComponent component = tabManager.get(item)
        tabbedPane.setSelectedComponent(component)
    }

    /**
     * Removes a tab from the Tabbed Pane for the given item
     * @param item
     */
    public void removeTab(AbstractUIItem item) {
        ULCComponent component = tabManager.get(item)
        if (component) {
            tabbedPane.remove(component)
            tabManager.remove(item)
        }
    }

    public boolean tabExists(AbstractUIItem abstractUIItem) {
        return tabManager.containsKey(abstractUIItem)
    }

    AbstractUIItem getAbstractItem(ULCComponent component) {
        AbstractUIItem abstractUIItem = null
        tabManager.each {k, v ->
            if (v == component)
                abstractUIItem = k
        }
        return abstractUIItem
    }

    public void updateTabbedPaneTitle(ULCTabbedPane tabbedPane, AbstractUIItem abstractUIItem) {
        ULCComponent component = tabManager.get(abstractUIItem)
        if (component) {
            int tabIndex = tabbedPane.indexOfComponent(component)
            if (tabIndex >= 0)
                tabbedPane.setTitleAt(tabIndex, abstractUIItem.createTitle())
        }

    }
}
