package org.pillarone.riskanalytics.application.util

import grails.util.Holders
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.pillarone.riskanalytics.application.help.MissingHelpException
import org.pillarone.riskanalytics.application.help.ComponentHelp
import org.pillarone.riskanalytics.core.example.component.ExampleInputOutputComponent
import org.pillarone.riskanalytics.core.util.ResourceBundleRegistry
import org.pillarone.riskanalytics.core.example.component.TestComponent


class ComponentHelpConfigTests extends GroovyTestCase {
    String configValue;

    @Override
    protected void setUp() {
        if (Holders.config) {
            configValue = Holders.config.get(ComponentHelp.DEFAULT_HELP_URL_CONFIG_KEY)
        }
        LocaleResources.setTestMode()
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.HELP, "org/pillarone/riskanalytics/application/help/ComponentHelp")
    }

    void testWithoutConfig() {
        assertNotNull(Holders.config)
        assertEquals "TestComponentURL", ComponentHelp.getHelpUrl(new TestComponent(), Locale.ENGLISH)
        //Remove any possible default help url, ensure it fails
        Holders.config.remove(ComponentHelp.DEFAULT_HELP_URL_CONFIG_KEY)
        shouldFail(MissingHelpException, {ComponentHelp.getHelpUrl(new ExampleInputOutputComponent(), Locale.ENGLISH)})
    }

    void testWithConfig() {
        assertNotNull(Holders.config)
        assertEquals "TestComponentURL", ComponentHelp.getHelpUrl(new TestComponent(), Locale.ENGLISH)
        //Add some dummy default help url to config, ensure it comes back ok from ComponentHelp
        Holders.config.put(ComponentHelp.DEFAULT_HELP_URL_CONFIG_KEY, "This is a default url")
        assertEquals("This is a default url", ComponentHelp.getHelpUrl(new ExampleInputOutputComponent(), Locale.ENGLISH))
    }

    @Override
    protected void tearDown() {
        //Re-set default help url in the config
        if (configValue) {

            Holders.config.put(ComponentHelp.DEFAULT_HELP_URL_CONFIG_KEY, configValue)
        } else {
            Holders.config.remove(ComponentHelp.DEFAULT_HELP_URL_CONFIG_KEY)
        }
        LocaleResources.clearTestMode()
    }
}
