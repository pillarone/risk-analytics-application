package org.pillarone.riskanalytics.application.util

import grails.util.Holders
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.application.help.ComponentHelp
import org.pillarone.riskanalytics.application.help.MissingHelpException
import org.pillarone.riskanalytics.core.example.component.ExampleInputOutputComponent
import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.core.util.ResourceBundleRegistry

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class ComponentHelpConfigTests {
    String configValue;

    @Before
    protected void setUp() {
        if (Holders.config) {
            configValue = Holders.config.get(ComponentHelp.DEFAULT_HELP_URL_CONFIG_KEY)
        }
        LocaleResources.setTestMode()
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.HELP, "org/pillarone/riskanalytics/application/help/ComponentHelp")
    }

    @Test
    void testWithoutConfig() {
        assertNotNull(Holders.config)
        assertEquals "TestComponentURL", ComponentHelp.getHelpUrl(new TestComponent(), Locale.ENGLISH)
        //Remove any possible default help url, ensure it fails
        Holders.config.remove(ComponentHelp.DEFAULT_HELP_URL_CONFIG_KEY)
        GroovyAssert.shouldFail(MissingHelpException, {ComponentHelp.getHelpUrl(new ExampleInputOutputComponent(), Locale.ENGLISH)}).getMessage()
    }

    @Test
    void testWithConfig() {
        assertNotNull(Holders.config)
        assertEquals "TestComponentURL", ComponentHelp.getHelpUrl(new TestComponent(), Locale.ENGLISH)
        //Add some dummy default help url to config, ensure it comes back ok from ComponentHelp
        Holders.config.put(ComponentHelp.DEFAULT_HELP_URL_CONFIG_KEY, "This is a default url")
        assertEquals("This is a default url", ComponentHelp.getHelpUrl(new ExampleInputOutputComponent(), Locale.ENGLISH))
    }

    @After
    void tearDown() {
        //Re-set default help url in the config
        if (configValue) {

            Holders.config.put(ComponentHelp.DEFAULT_HELP_URL_CONFIG_KEY, configValue)
        } else {
            Holders.config.remove(ComponentHelp.DEFAULT_HELP_URL_CONFIG_KEY)
        }
        LocaleResources.clearTestMode()
    }
}
