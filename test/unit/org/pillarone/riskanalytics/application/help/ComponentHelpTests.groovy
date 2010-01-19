package org.pillarone.riskanalytics.application.help

import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.core.example.component.ExampleInputOutputComponent;

class ComponentHelpTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testGetComponentHelp() {
        assertEquals "TestComponentURL", ComponentHelp.getHelpUrl(new TestComponent(), Locale.ENGLISH)
        shouldFail MissingHelpException, {ComponentHelp.getHelpUrl(new ExampleInputOutputComponent(), Locale.ENGLISH)}

    }
}