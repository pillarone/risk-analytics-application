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
        String newTitle = "$item.name v${item.versionNumber.toString()}"
        int index = getTabIndexForName(tabbedPane, newTitle)
        if (item.isChanged()) {
            newTitle = "${newTitle} *"
        }
        if (index >= 0) {
            tabbedPane.setTitleAt(index, newTitle)
        }
    }

    public void itemChanged(Parameterization item) {
        String newTitle = "$item.name v${item.versionNumber.toString()}"
        int index = getTabIndexForName(tabbedPane, newTitle)
        if (item.isChanged()) {
            newTitle = "${newTitle} *"
        }
        if (index >= 0) {
            tabbedPane.setTitleAt(index, newTitle)
        }
    }

    public void itemChanged(ModellingItem item) {

    }

    private int getTabIndexForName(ULCTabbedPane tabbedPane, String tabTitle) {
        int tabIndex = -1
        tabbedPane.tabCount.times {
            if (tabbedPane?.getTitleAt(it)?.startsWith(tabTitle)) {
                tabIndex = it
            }
        }
        return tabIndex
    }
}