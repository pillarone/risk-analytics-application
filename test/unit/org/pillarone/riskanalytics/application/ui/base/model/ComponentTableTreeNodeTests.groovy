package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.component.TestComponent

class ComponentTableTreeNodeTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testNodeName() {
        ComponentTableTreeNode node = new ComponentTableTreeNode(new TestComponent(name: "componentName"), "nodeName")
        assertEquals("nodeName", node.name)
    }

    void testDisplayName() {
        ComponentTableTreeNode node = new ComponentTableTreeNode(new TestComponent(name: "componentName"), "nodeName")
        assertEquals("node name", node.displayName)

        node = new ComponentTableTreeNode(new TestComponent(name: "subCompo"), "subCompo")
        assertEquals("Compo", node.displayName)
    }

    void testGetValueAt() {
        ComponentTableTreeNode node = new ComponentTableTreeNode(new TestComponent(name: "componentName"), "nodeName")
        assertEquals("nodeName", node.name)
        assertEquals("node name", node.getValueAt(0))
    }
}