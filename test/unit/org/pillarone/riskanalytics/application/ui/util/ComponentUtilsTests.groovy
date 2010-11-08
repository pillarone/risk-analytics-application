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
        assertEquals "Hello World", name

        name = "helloWorld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subhelloWorld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "hello World", name

        name = "Helloworld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subHelloworld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "Helloworld", name


        name = "helloworld"
        name = ComponentUtils.getSubComponentName(name)
        assertEquals "subhelloworld", name
        name = I18NUtils.formatDisplayName(name)
        assertEquals "helloworld", name

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


}
