package org.pillarone.riskanalytics.application.ui.base.model

import models.core.CoreModel
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.core.example.component.TestComponent

class ComponentTableTreeNodeTests extends GroovyTestCase {

    void setUp() {
        UserContext.setAttribute("SESSION_LOCAL_LOCALE", Locale.getDefault())
    }


    void testNodeName() {
        ComponentTableTreeNode node = new ComponentTableTreeNode(new TestComponent(name: "componentName"), CoreModel, "nodeName")
        assertEquals("nodeName", node.name)
    }

    void testDisplayName() {
        ComponentTableTreeNode node = new ComponentTableTreeNode(new TestComponent(name: "componentName"), CoreModel, "nodeName")
        assertEquals("Node name display value", node.displayName)
        assertEquals("Node name tooltip", node.toolTip)

        node = new ComponentTableTreeNode(new TestComponent(name: "subCompo"), CoreModel, "subCompo")
        //todo ask stefan how to add subCompo property to resource
//        assertEquals("Compo", node.displayName)
        assertEquals("Node name display value", node.displayName)
    }

    void testGetValueAt() {
        ComponentTableTreeNode node = new ComponentTableTreeNode(new TestComponent(name: "componentName"), CoreModel, "nodeName")
        assertEquals("nodeName", node.name)
        assertEquals("Node name display value", node.getValueAt(0))
    }
}