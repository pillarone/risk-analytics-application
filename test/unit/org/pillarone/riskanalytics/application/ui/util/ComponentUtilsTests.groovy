package org.pillarone.riskanalytics.application.ui.util

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ComponentUtilsTests extends GroovyTestCase {

    void testGetName() {
        String name = "HelloWorld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHelloWorld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "hello world", name

        name = "helloworld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHelloworld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "helloworld", name

        name = "hello world"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHelloWorld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "hello world", name

        name = "HELLOWORLD"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHELLOWORLD", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "HELLOWORLD", name

        name = "HELLOWorld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHELLOWorld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "HELLO world", name
    }

}
