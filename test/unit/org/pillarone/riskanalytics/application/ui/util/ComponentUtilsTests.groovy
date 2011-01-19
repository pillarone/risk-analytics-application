package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ComponentUtilsTests extends GroovyTestCase {

    void testGetName() {
        String name = "HelloWorld"
        name = I18NUtils.formatDisplayName(name)
        assertEquals "hello world", name

        name = "subHelloworld"
        name = I18NUtils.formatDisplayName(name)
        assertEquals "helloworld", name

        name = "subHelloWorld"
        name = I18NUtils.formatDisplayName(name)
        assertEquals "hello world", name

        name = "subHELLOWORLD"
        name = I18NUtils.formatDisplayName(name)
        assertEquals "HELLOWORLD", name

        name = "subHELLOWorld"
        name = I18NUtils.formatDisplayName(name)
        assertEquals "HELLO world", name

        name = "Options 3 and 4"
        name = ComponentUtils.getSubComponentName(name)
        println "${name}"
        assertEquals "sub_Options_3_and_4", name
        name = I18NUtils.formatDisplayName(name)
        println "${name}"
        assertEquals "Options 3 and 4", name

        name = "LOB 2"
        name = ComponentUtils.getSubComponentName(name)
        println "${name}"
        assertEquals "sub_LOB_2", name
        name = I18NUtils.formatDisplayName(name)
        println "${name}"
        assertEquals "LOB 2", name

        //"LOB 2"
        name = "Hello World"
        name = ComponentUtils.getSubComponentName(name)
        println "${name}"
        assertEquals "sub_Hello_World", name
        name = I18NUtils.formatDisplayName(name)
        println "${name}"
        assertEquals "Hello World", name

        name = "HelloWorld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "sub_HelloWorld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "HelloWorld", name

        name = "helloworld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "sub_helloworld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "helloworld", name

        name = "hello world"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "sub_hello_world", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "hello world", name

        name = "HELLOWORLD"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "sub_HELLOWORLD", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "HELLOWORLD", name

        name = "HELLOWorld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "sub_HELLOWorld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "HELLOWorld", name

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
