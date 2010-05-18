package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.ULCTabbedPane
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

class MarkItemAsUnsavedListener implements IModellingItemChangeListener {
    ULCCloseableTabbedPane tabbedPane
    int paneIndex

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
            newTitle += " *"
        }
        TabbedPaneGuiHelper.updateTabbedPaneTitle(tabbedPane, title, newTitle)
    }

    public void itemSaved(ModellingItem item) {
        
    }
}

class TabbedPaneGuiHelper {

    public static void updateTabbedPaneTitle(ULCCloseableTabbedPane tabbedPane, String oldTitle, String newTitle) {
        int index = getTabIndexForName(tabbedPane, oldTitle)
        if (index >= 0) {
            tabbedPane.setTitleAt(index, newTitle)
        } else {
            int frameId = tabbedPane.findFrameID(oldTitle)
            if (frameId > 0) {
                ULCCloseableTabbedPane dependantTabbedPane = tabbedPane.getDependantTabbedPane(frameId - 1)
                dependantTabbedPane.setTitleAt(0, newTitle)
            }
        }
    }

    private static int getTabIndexForName(ULCTabbedPane tabbedPane, String tabTitle) {
        int tabIndex = -1
        tabbedPane.tabCount.times {
            if (tabbedPane?.getTitleAt(it)?.startsWith(tabTitle)) {
                tabIndex = it
            }
        }
        return tabIndex
    }
}