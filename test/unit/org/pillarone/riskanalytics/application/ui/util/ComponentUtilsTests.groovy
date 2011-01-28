package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ComponentUtilsTests extends GroovyTestCase {

    void testGetName() {
        String name = "HelloWorld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHelloWorld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "Hello World", name

        name = "helloWorld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHelloWorld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "Hello World", name

        name = "Helloworld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHelloworld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "Helloworld", name


        name = "helloworld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHelloworld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "Helloworld", name

        name = "HELLOWORLD"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHELLOWORLD", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "HELLOWORLD", name

        name = "HELLOWorld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHELLOWorld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "HELLO World", name


    }

    void testIntersection() {
        SimpleTableTreeNode node0 = new SimpleTableTreeNode("node0")
        SimpleTableTreeNode node1 = new SimpleTableTreeNode("node1")
        SimpleTableTreeNode node2 = new SimpleTableTreeNode("node2")
        SimpleTableTreeNode node3 = new SimpleTableTreeNode("node3")

        assertEquals ComponentUtils.intersection([[node0], [node0, node1, node2], [node0, node1, node2, node3]]), [node0]
        assertEquals ComponentUtils.intersection([[node3], [node0, node1, node2], [node0, node1, node2, node3]]), []
        assertEquals ComponentUtils.intersection([[node0, node1], [node0, node1, node2], [node0, node1, node2, node3]]), [node0, node1]
        assertEquals ComponentUtils.intersection([[node0, node1, node2], [node0, node1, node2], [node0, node1, node2, node3]]), [node0, node1, node2]
        assertEquals ComponentUtils.intersection([[node0, node1, node2], [node0, node1, node2], [node0, node1, node2]]), [node0, node1, node2]
        assertEquals ComponentUtils.intersection([[node0, node2], [node0, node1, node2], [node0, node1, node2]]), [node0]
        println("${ComponentUtils.intersection([[[node0, node1, node2]]])}")


    }

}
