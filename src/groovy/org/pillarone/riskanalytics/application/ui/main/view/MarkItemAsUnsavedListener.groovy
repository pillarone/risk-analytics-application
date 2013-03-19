package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCTabbedPane
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

class MarkItemAsUnsavedListener implements IModellingItemChangeListener {
    private ULCTabbedPane tabbedPane
    static String UNSAVED_MARK = " *"
    private TabbedPaneManager tabbedPaneManager
    private AbstractUIItem abstractUIItem

    public MarkItemAsUnsavedListener(TabbedPaneManager tabbedPaneManager, ULCTabbedPane tabbedPane, AbstractUIItem abstractUIItem) {
        this.tabbedPaneManager = tabbedPaneManager
        this.tabbedPane = tabbedPane
        this.abstractUIItem = abstractUIItem
    }

    public void itemChanged(ModellingItem modellingItem) {
        tabbedPaneManager.updateTabbedPaneTitle(tabbedPane, abstractUIItem)
    }

    public void itemSaved(ModellingItem modellingItem) {
        update(abstractUIItem)
    }

    public void update(AbstractUIItem abstractUIItem) {
        tabbedPaneManager.updateTabbedPaneTitle(tabbedPane, abstractUIItem)
    }

    public void update(BatchUIItem batchUIItem) {
        tabbedPaneManager.updateTabbedPaneTitle(tabbedPane, batchUIItem)
        batchUIItem.mainModel.navigationTableTreeModel.refreshBatchNode()
    }

    public static String removeUnsavedMark(String title) {
        if (title.endsWith(UNSAVED_MARK)) return title.substring(0, title.indexOf(UNSAVED_MARK))
        return title
    }

}

