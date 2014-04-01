package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.pillarone.riskanalytics.application.ui.main.action.SaveAction
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert

class TabbedPaneManager {

    private ULCTabbedPane tabbedPane
    private DependentFramesManager dependentFramesManager
    private Map<AbstractUIItem, ULCComponent> tabManager = [:]

    //keep a map of open items to avoid to compare titles to find the tabs

    TabbedPaneManager(ULCTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane
        this.dependentFramesManager = new DependentFramesManager(this.tabbedPane)
    }

    /**
     * Creates a new tab for the given item
     * The content of a card is currently a TabbedPane.
     * A TabbedPaneManager needs to be created here as well.
     * @param model
     */
    public void addTab(AbstractUIItem item) {
        ULCContainer view = item.createDetailView()
        def wrapped = new ULCScrollPane(view, ULCScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, ULCScrollPane.HORIZONTAL_SCROLLBAR_NEVER)
        tabbedPane.addTab(item.createTitle(), item.icon, wrapped)
        int tabIndex = tabbedPane.tabCount - 1
        tabbedPane.selectedIndex = tabIndex
        tabManager[item] = wrapped
        item.addModellingItemChangeListener new MarkItemAsUnsavedListener(this, tabbedPane, item)
        tabbedPane.setToolTipTextAt(tabIndex, item.toolTip)
    }

    public void closeTab(AbstractUIItem abstractUIItem) {
        if (abstractUIItem.changed) {
            boolean closeTab = true
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tabManager[abstractUIItem]), "itemChanged")
            alert.addWindowListener([windowClosing: { WindowEvent windowEvent ->
                def value = windowEvent.source.value
                if (value.equals(alert.firstButtonLabel)) {
                    SaveAction saveAction = new SaveAction(tabbedPane, abstractUIItem.mainModel, abstractUIItem)
                    saveAction.doActionPerformed(new ActionEvent(this, "save"))
                } else if (value.equals(alert.thirdButtonLabel)) {
                    closeTab = false

                } else {
                    abstractUIItem.unload()

                }
                if (closeTab) {
                    removeTab(abstractUIItem)
                    abstractUIItem.close()
                } else {
                    selectTab(abstractUIItem)
                }
            }] as IWindowListener)
            alert.show()
        } else {
            removeTab(abstractUIItem)
            abstractUIItem.close()
        }
    }

    /**
     * Opens and selects the card for the given model
     * @param model the model to open
     */
    public void selectTab(AbstractUIItem item) {
        ULCComponent component = tabManager.get(item)
        if (tabbedPane.indexOfComponent(component) >= 0) {
            tabbedPane.setSelectedComponent(component)
        } else {
            dependentFramesManager.selectTab(item)
        }
    }

    /**
     * Removes a tab from the Tabbed Pane for the given item
     * @param item
     */
    public void removeTab(AbstractUIItem item) {
        ULCComponent component = tabManager.get(item)
        if (component) {
            if (tabbedPane.indexOfComponent(component) >= 0)
                tabbedPane.remove(component)
            else
                dependentFramesManager.closeTab(item)
            tabManager.remove(item)
        }
    }

    public boolean tabExists(AbstractUIItem abstractUIItem) {
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

    public void updateTabbedPaneTitle(ULCTabbedPane tabbedPane, AbstractUIItem abstractUIItem) {
        ULCComponent component = tabManager.get(abstractUIItem)
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
