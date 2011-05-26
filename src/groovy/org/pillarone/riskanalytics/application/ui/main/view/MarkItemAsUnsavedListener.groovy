package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.*
import com.ulcjava.base.application.ULCTabbedPane
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem

class MarkItemAsUnsavedListener implements IModellingItemChangeListener {
    ULCTabbedPane tabbedPane
    static String UNSAVED_MARK = " *"
    TabbedPaneManager tabbedPaneManager
    AbstractUIItem abstractUIItem

    public MarkItemAsUnsavedListener(TabbedPaneManager tabbedPaneManager, ULCTabbedPane tabbedPane, AbstractUIItem abstractUIItem) {
        this.tabbedPaneManager = tabbedPaneManager
        this.tabbedPane = tabbedPane
        this.abstractUIItem = abstractUIItem
    }

    public void itemChanged(ModellingItem modellingItem) {
        tabbedPaneManager.updateTabbedPaneTitle(tabbedPane, abstractUIItem)
    }

     public void itemSaved(ModellingItem modellingItem) {
          tabbedPaneManager.updateTabbedPaneTitle(tabbedPane, abstractUIItem)
     }

}

