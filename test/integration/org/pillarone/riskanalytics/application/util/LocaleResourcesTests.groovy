package org.pillarone.riskanalytics.application.util

import org.pillarone.riskanalytics.core.user.UserSettings

class LocaleResourcesTests extends GroovyTestCase {


    void testGetLocale_language() {
        Locale locale = LocaleResources.buildLocale(new UserSettings(language: 'fr'))
        assert Locale.FRENCH == locale
    }

    void testGetLocale_language_and_country() {
        Locale locale = LocaleResources.buildLocale(new UserSettings(language: 'de_CH'))
        assert new Locale('de', 'CH') == locale
    }
}
