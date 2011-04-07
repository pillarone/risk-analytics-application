package org.pillarone.riskanalytics.application.ui.base

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.server.ULCSession
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ULCDTabbedPaneTests extends AbstractP1RATTestCase {

    public void testView() {
//        Thread.sleep 1000
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
        println pane1.getId();
        println pane2.getId();
        println pane3.getId();
        println ULCSession.currentSession().getRegistry().find(tabbedPane.getId())

        println tabbedPane.getComponentIndex(ULCSession.currentSession().getRegistry().find(pane3.getId()))
        println tabbedPane.getComponentIndex(ULCSession.currentSession().getRegistry().find(pane2.getId()))
        println tabbedPane.getComponentIndex(ULCSession.currentSession().getRegistry().find(pane1.getId()))
        println tabbedPane.getComponentIndex(pane1)


        return tabbedPane
    }


}
