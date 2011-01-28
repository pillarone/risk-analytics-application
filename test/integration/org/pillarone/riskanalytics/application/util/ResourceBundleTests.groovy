package org.pillarone.riskanalytics.application.util

class ResourceBundleTests extends GroovyTestCase {

    @Override protected void setUp() {
        super.setUp()
        LocaleResources.setTestMode()
    }

    @Override protected void tearDown() {
        super.tearDown()
        LocaleResources.clearTestMode()
        ResourceBundleFactory.reset()
    }

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
