package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.util.ResourceBundleRegistry

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class I18NUtilsTests extends GroovyTestCase {

    protected void setUp() {
        super.setUp();
        LocaleResources.setTestMode()
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.RESOURCE, "org.pillarone.riskanalytics.application.i18nutilsTests")
    }


    public void testGetExceptionText() {//exceptionKey
        assertTrue I18NUtils.getExceptionText("exception text").indexOf("exception text") > 0
        assertTrue I18NUtils.getExceptionText("['exception0Key']").indexOf("exception message") > 0
        assertTrue I18NUtils.getExceptionText("['exception1Key', 'key1']").indexOf("exception message key1") > 0
        assertTrue I18NUtils.getExceptionText("['exception2Key', 'key1', 'key2']").indexOf("exception message key1 and key2") > 0
    }

}
