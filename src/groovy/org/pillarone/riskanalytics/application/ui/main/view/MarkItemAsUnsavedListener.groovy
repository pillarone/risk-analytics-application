package org.pillarone.riskanalytics.application.ui.main.view

import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

class MarkItemAsUnsavedListener implements IModellingItemChangeListener {
    static final String UNSAVED_MARK = " *"
    private TabbedPaneManager tabbedPaneManager
    private ModellingUIItem modellingUIItem

    MarkItemAsUnsavedListener(TabbedPaneManager tabbedPaneManager, ModellingUIItem modellingUIItem) {
        this.tabbedPaneManager = tabbedPaneManager
        this.modellingUIItem = modellingUIItem
    }

    void itemChanged(ModellingItem modellingItem) {
        tabbedPaneManager.updateTabbedPaneTitle(modellingUIItem)
    }

    void itemSaved(ModellingItem modellingItem) {
        tabbedPaneManager.updateTabbedPaneTitle(modellingUIItem)
    }


    static String removeUnsavedMark(String title) {
        if (title.endsWith(UNSAVED_MARK)) {
            return title.substring(0, title.indexOf(UNSAVED_MARK))
        }
        return title
    }

}

