package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.ULCTabbedPane
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.core.simulation.item.Batch

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TabbedPaneManagerUnitTests extends AbstractP1RATTestCase {
    RiskAnalyticsMainModel model = new RiskAnalyticsMainModel()

    void testView() {}

    @Override
    ULCComponent createContentPane() {
        ULCTabbedPane tabbedPane = new ULCDetachableTabbedPane()
        TabbedPaneManager tabbedPaneManager = new TabbedPaneManager(tabbedPane)

        assertEquals "tabCount must be 0", 0, tabbedPane.tabCount

        BatchUIItem batchUIItem = createUIItem('item1')

        tabbedPaneManager.addTab(batchUIItem)
        assertTrue tabbedPane.selectedIndex == 0

        assertEquals "tabCount must be 1", 1, tabbedPane.tabCount

        assertTrue tabbedPaneManager.tabExists(batchUIItem)

        BatchUIItem batchUIItem2 = createUIItem('item2')
        assertFalse tabbedPaneManager.tabExists(batchUIItem2)
        tabbedPaneManager.addTab(batchUIItem2)

        assertEquals "tabCount must be 2", 2, tabbedPane.tabCount
        assertTrue tabbedPane.selectedIndex == 1

        tabbedPaneManager.selectTab(batchUIItem)
        assertTrue tabbedPane.selectedIndex == 0

        tabbedPaneManager.removeTab(batchUIItem2)
        assertEquals "tabCount must be 1", 1, tabbedPane.tabCount
        assertTrue tabbedPaneManager.tabExists(batchUIItem)
        assertFalse tabbedPaneManager.tabExists(batchUIItem2)

        return new ULCBoxPane()
    }

    BatchUIItem createUIItem(String name) {
        new TestUIItem(name)
    }

    static class TestUIItem extends BatchUIItem {

        TestUIItem(String name) {
            super(new RiskAnalyticsMainModel(), new Batch(name))
        }

        @Override
        ULCContainer createDetailView() {
            null
        }
    }

}
