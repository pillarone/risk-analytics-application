package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.ULCTabbedPane

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
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