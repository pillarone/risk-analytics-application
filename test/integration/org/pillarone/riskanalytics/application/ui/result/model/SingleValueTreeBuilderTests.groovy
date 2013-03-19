package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.result.view.SingleCollectorIterationRootNode
import org.pillarone.riskanalytics.application.util.LocaleResources

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleValueTreeBuilderTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testTreeStructure() {
        Map singleValueResultsMap = [:]
        singleValueResultsMap[0] = [["net", 1, "paid", 1, 0, 0], ["net", 11, "paid", 1, 0, 1], ["net", 111, "paid", 1, 0, 2]]
        singleValueResultsMap[1] = [["net", 2, "paid", 1, 0, 0], ["net", 22, "paid", 1, 0, 1], ["net", 222, "paid", 1, 0, 2]]
        SingleValueTreeBuilder builder = new SingleValueTreeBuilder(singleValueResultsMap, 1, 1, 2, 1)
        builder.build()

        assertNotNull builder.root

        assertEquals 1, builder.iterations
        assertEquals 1, builder.periodCount
        assertEquals 2, builder.selectedNodesSize

        assertEquals 1, builder.root.childCount

        assertTrue builder.root.getChildAt(0) instanceof SingleCollectorIterationRootNode

        assertEquals "iteration: 1", builder.root.getChildAt(0).getValueAt(0)

        //TODO: aggregation currently disabled
//        assertEquals 123, builder.root.getChildAt(0).getValueAtIndex(1)
//        assertEquals 246, builder.root.getChildAt(0).getValueAtIndex(2)

        assertEquals 3, builder.root.getChildAt(0).childCount

        ITableTreeNode node0 = builder.root.getChildAt(0).getChildAt(0)
        assertEquals "1", node0.getValueAt(0)
        assertEquals 1, node0.getValueAtIndex(1)
        assertEquals 2, node0.getValueAtIndex(2)

        ITableTreeNode node1 = builder.root.getChildAt(0).getChildAt(1)
        assertEquals "2", node1.getValueAt(0)
        assertEquals 11, node1.getValueAtIndex(1)
        assertEquals 22, node1.getValueAtIndex(2)

        ITableTreeNode node2 = builder.root.getChildAt(0).getChildAt(2)
        assertEquals "3", node2.getValueAt(0)
        assertEquals 111, node2.getValueAtIndex(1)
        assertEquals 222, node2.getValueAtIndex(2)

    }
}
