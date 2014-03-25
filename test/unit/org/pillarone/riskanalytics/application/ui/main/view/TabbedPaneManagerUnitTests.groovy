package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTabbedPane
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TabbedPaneManagerUnitTests extends AbstractP1RATTestCase {

    public void testView() {

    }

    @Override
    ULCComponent createContentPane() {
        ULCTabbedPane tabbedPane = new ULCDetachableTabbedPane()
        TabbedPaneManager tabbedPaneManager = new TabbedPaneManager(tabbedPane)

        assertEquals "tabCount must be 0", 0, tabbedPane.getTabCount()

        RiskAnalyticsMainModel model = new RiskAnalyticsMainModel()
        BatchUIItem batchUIItem = new BatchUIItem(model, null)

        tabbedPaneManager.addTab(batchUIItem)
        assertTrue tabbedPane.getSelectedIndex() == 0

        assertEquals "tabCount must be 1", 1, tabbedPane.getTabCount()

        assertTrue tabbedPaneManager.tabExists(batchUIItem)

        BatchUIItem batchUIItem2 = new BatchUIItem(model, null)
        assertFalse tabbedPaneManager.tabExists(batchUIItem2)
        tabbedPaneManager.addTab(batchUIItem2)

        assertEquals "tabCount must be 2", 2, tabbedPane.getTabCount()
        assertTrue tabbedPane.getSelectedIndex() == 1

        tabbedPaneManager.selectTab(batchUIItem)
        assertTrue tabbedPane.getSelectedIndex() == 0

        tabbedPaneManager.removeTab(batchUIItem2)
        assertEquals "tabCount must be 1", 1, tabbedPane.getTabCount()
        assertTrue tabbedPaneManager.tabExists(batchUIItem)
        assertFalse tabbedPaneManager.tabExists(batchUIItem2)

        return new ULCBoxPane()
    }


}
