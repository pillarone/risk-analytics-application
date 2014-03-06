package org.pillarone.riskanalytics.application.ui.base

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ULCDTabbedPaneTests extends AbstractP1RATTestCase {

    public void testView() {
        //TODO test something
    }

    @Override
    ULCComponent createContentPane() {
        ULCDetachableTabbedPane tabbedPane = new ULCDetachableTabbedPane(name: 'tabbedPane')
        ULCCloseableTabbedPane pane1 = new ULCCloseableTabbedPane()
        ULCCloseableTabbedPane pane2 = new ULCCloseableTabbedPane()
        ULCCloseableTabbedPane pane3 = new ULCCloseableTabbedPane()
        tabbedPane.addTab("Tab1", pane1, true)
        tabbedPane.addTab("Tab2", pane2, true)
        tabbedPane.addTab("Tab3", pane3, true)
        return tabbedPane
    }
}
