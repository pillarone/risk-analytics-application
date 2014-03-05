package org.pillarone.riskanalytics.application.help

import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.component.ExampleInputOutputComponent
import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.core.util.ResourceBundleRegistry

class ComponentHelpTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode(true)
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.HELP, "org/pillarone/riskanalytics/application/help/ComponentHelp")
    }

    protected void tearDown() {
        LocaleResources.setTestMode(false)
    }

    void testGetComponentHelp() {
        assertEquals "TestComponentURL", ComponentHelp.getHelpUrl(new TestComponent(), Locale.ENGLISH)
        shouldFail MissingHelpException, {ComponentHelp.getHelpUrl(new ExampleInputOutputComponent(), Locale.ENGLISH)}

    }
}