package org.pillarone.riskanalytics.application.util

import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

class ResourceBundleTests {

    @Before
    void setUp() {
        LocaleResources.setTestMode()
    }

    @After
    void tearDown() {
        LocaleResources.clearTestMode()
        ResourceBundleFactory.reset()
    }

    @Test
    void testReplacements() {
        ResourceBundle bundle = LocaleResources.getBundle("org.pillarone.riskanalytics.application.applicationResources")

        assertFalse bundle instanceof ResourceBundleDecorator
        assertEquals "About RiskAnalytics", bundle.getString("AboutDialog.about")
        assertEquals "System Properties", bundle.getString("AboutDialog.sysProps")

        ResourceBundleFactory.addReplacement("org.pillarone.riskanalytics.application.applicationResources", "org.pillarone.riskanalytics.application.example.resources")

        bundle = LocaleResources.getBundle("org.pillarone.riskanalytics.application.applicationResources")

        assertTrue bundle instanceof ResourceBundleDecorator
        assertEquals "Test", bundle.getString("AboutDialog.about")
        assertEquals "System Properties", bundle.getString("AboutDialog.sysProps")

    }
}
