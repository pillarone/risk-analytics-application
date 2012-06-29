package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNodeFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.core.components.ComponentUtils

class SimpleTableTreeNodeTests extends GroovyTestCase {

    void testSetValue() {
        shouldFail(UnsupportedOperationException, {new SimpleTableTreeNode("testNode").setValueAt("bar", 0)})
    }

    void testInsertNode() {
        SimpleTableTreeNode node = new SimpleTableTreeNode("testNode")
        SimpleTableTreeNode childNode = new SimpleTableTreeNode("childNode")
        node.insert(childNode, 0)
        assertEquals(1, node.childCount)
        assertSame node, childNode.parent
    }

    void testRemoveNode() {
        SimpleTableTreeNode node = new SimpleTableTreeNode("testNode")
        SimpleTableTreeNode childNode = new SimpleTableTreeNode("childNode")
        node.add(childNode)
        assertEquals(1, node.childCount)
        assertSame node, childNode.parent

        node.remove(0)
        assertEquals(0, node.childCount)
        assertNull(childNode.parent)
    }

    void testValue() {
        SimpleTableTreeNode node = new SimpleTableTreeNode("testNode")
        node.expanded = false
        SimpleTableTreeNode childNode1 = new SimpleTableTreeNode("childNode1")
        SimpleTableTreeNode childNode2 = new SimpleTableTreeNode("childNode2")
        node.add(childNode1)
        node.add(childNode2)

        assertEquals(ComponentUtils.getNormalizedName("testNode"), node.getValueAt(0))
        assertEquals("", node.getValueAt(1))

        node.add(ParameterizationNodeFactory.getNode([ParameterHolderFactory.getHolder("component:parm1", 0, "value1")], null))
        node.add(ParameterizationNodeFactory.getNode([ParameterHolderFactory.getHolder("component:parm2", 0, "value2")], null))

        assertEquals("value1 value2", node.getValueAt(1))

        node.expanded = true
        assertEquals("node is expanded, thus empty string as cell value", "", node.getValueAt(1))
    }

    void testPath() {
        SimpleTableTreeNode node = new SimpleTableTreeNode("testNode")
        SimpleTableTreeNode childNode = new SimpleTableTreeNode("childNode")
        node.add(childNode)
        assertEquals "testNode", node.path
        assertEquals "testNode:childNode", childNode.path
    }

    void testDisplayPath() {
        SimpleTableTreeNode node = new SimpleTableTreeNode("testNode")
        SimpleTableTreeNode childNode = new SimpleTableTreeNode("childNode")
        node.add(childNode)
        assertEquals ComponentUtils.getNormalizedName("testNode"), node.getDisplayPath()
        assertEquals "${ComponentUtils.getNormalizedName("testNode")} $SimpleTableTreeNode.PATH_SEPARATOR ${ComponentUtils.getNormalizedName("childNode")}", childNode.getDisplayPath()
    }

    void testTreePath() {
        SimpleTableTreeNode node0 = new SimpleTableTreeNode("node0")
        SimpleTableTreeNode node1 = new SimpleTableTreeNode("node1")
        SimpleTableTreeNode node3 = new SimpleTableTreeNode("node3")
        node1.parent = node0
        node3.parent = node1

        assertEquals 3, node3.treePath.size()
        assertEquals node0, node3.treePath[0]
        assertEquals node1, node3.treePath[1]
        assertEquals node3, node3.treePath[2]
    }

    void testShortDisplayPath() {
        SimpleTableTreeNode node0 = new SimpleTableTreeNode("node0")
        SimpleTableTreeNode node1 = new SimpleTableTreeNode("node1")
        SimpleTableTreeNode node2 = new SimpleTableTreeNode("node2")

        assertEquals ComponentUtils.getNormalizedName("node1"), node1.getShortDisplayPath([node0, node1, node2])

        node1.parent = node0
        node2.parent = node0
        SimpleTableTreeNode node3 = new SimpleTableTreeNode("node3")
        SimpleTableTreeNode node4 = new SimpleTableTreeNode("node4")
        node3.parent = node1
        node4.parent = node1
        SimpleTableTreeNode node5 = new SimpleTableTreeNode("node5")
        node5.parent = node2

        assertEquals "${ComponentUtils.getNormalizedName("node1")} $SimpleTableTreeNode.PATH_SEPARATOR ${ComponentUtils.getNormalizedName("node3")}", node3.getShortDisplayPath([node5])
        assertEquals "${ComponentUtils.getNormalizedName("node3")}", node3.getShortDisplayPath([node4])
        assertEquals "${ComponentUtils.getNormalizedName("node1")} $SimpleTableTreeNode.PATH_SEPARATOR ${ComponentUtils.getNormalizedName("node3")}", node3.getShortDisplayPath([node2])
        assertEquals node2.displayPath, node2.getShortDisplayPath([node5])

        assertEquals "${ComponentUtils.getNormalizedName("node1")} $SimpleTableTreeNode.PATH_SEPARATOR ${ComponentUtils.getNormalizedName("node3")}", node3.getShortDisplayPath([node5, node4])
        //todo sca
//        assertEquals "node1 > node3", node3.getShortDisplayPath([node4,node5])
        assertEquals ComponentUtils.getNormalizedName("node3"), node3.getShortDisplayPath([node1])
        assertEquals ComponentUtils.getNormalizedName("node3"), node3.getShortDisplayPath([node3])
    }
}