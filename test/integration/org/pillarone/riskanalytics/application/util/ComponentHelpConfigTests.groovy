package org.pillarone.riskanalytics.application.util

import grails.util.Holders
import org.pillarone.riskanalytics.application.help.MissingHelpException
import org.pillarone.riskanalytics.core.example.component.ExampleInputOutputComponent
import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.core.util.ResourceBundleRegistry

import static org.pillarone.riskanalytics.application.help.ComponentHelp.DEFAULT_HELP_URL_CONFIG_KEY
import static org.pillarone.riskanalytics.application.help.ComponentHelp.getHelpUrl

class ComponentHelpConfigTests extends GroovyTestCase {
    String configValue;

    @Override
    protected void setUp() {
        if (Holders.config) {
            configValue = Holders.config.get(DEFAULT_HELP_URL_CONFIG_KEY)
        }
        LocaleResources.testMode = true
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.HELP, "org/pillarone/riskanalytics/application/help/ComponentHelp")
    }

    void testWithoutConfig() {
        assertNotNull(Holders.config)
        assertEquals "TestComponentURL", getHelpUrl(new TestComponent(), Locale.ENGLISH)
        //Remove any possible default help url, ensure it fails
        Holders.config.remove(DEFAULT_HELP_URL_CONFIG_KEY)
        shouldFail(MissingHelpException, { getHelpUrl(new ExampleInputOutputComponent(), Locale.ENGLISH) })
    }

    void testWithConfig() {
        assertNotNull(Holders.config)
        assertEquals "TestComponentURL", getHelpUrl(new TestComponent(), Locale.ENGLISH)
        //Add some dummy default help url to config, ensure it comes back ok from ComponentHelp
        Holders.config.put(DEFAULT_HELP_URL_CONFIG_KEY, "This is a default url")
        assertEquals("This is a default url", getHelpUrl(new ExampleInputOutputComponent(), Locale.ENGLISH))
    }

    @Override
    protected void tearDown() {
        //Re-set default help url in the config
        if (configValue) {
            Holders.config.put(DEFAULT_HELP_URL_CONFIG_KEY, configValue)
        } else {
            Holders.config.remove(DEFAULT_HELP_URL_CONFIG_KEY)
        }
        LocaleResources.testMode = false
    }
}
