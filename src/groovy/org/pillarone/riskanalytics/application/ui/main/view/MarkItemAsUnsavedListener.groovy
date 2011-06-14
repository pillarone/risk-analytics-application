package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.util.GroovyUtils

class MarkItemAsUnsavedListener implements IModellingItemChangeListener {
    ULCCloseableTabbedPane tabbedPane
    int paneIndex
    static String UNSAVED_MARK = " *"

    public MarkItemAsUnsavedListener(ULCCloseableTabbedPane tabbedPane, int paneIndex) {
        this.@tabbedPane = tabbedPane
        this.@paneIndex = paneIndex
    }

    public void itemChanged(ResultConfiguration item) {
        updateUnsavedTabbedPaneTitle(item)
    }


    public void itemChanged(Parameterization item) {
        updateUnsavedTabbedPaneTitle(item)
    }

    public void itemChanged(ModellingItem item) {
    }

    private void updateUnsavedTabbedPaneTitle(ModellingItem item) {
        String title = "$item.name v${item.versionNumber.toString()}"
        String newTitle = title
        if (item.isChanged()) {
            newTitle += UNSAVED_MARK
        }
        TabbedPaneGuiHelper.updateTabbedPaneTitle(tabbedPane, title, newTitle)
    }

    public void itemSaved(ModellingItem item) {
        // workaround for PMO-1618, after merging with refactoring branch, issue will be solved
        if (GroovyUtils.getProperties(item).containsKey("versionNumber")) {
            String title = "$item.name v${item.versionNumber.toString()}"
            TabbedPaneGuiHelper.updateTabbedPaneTitle(tabbedPane, title + UNSAVED_MARK, title)
        }
    }
}

