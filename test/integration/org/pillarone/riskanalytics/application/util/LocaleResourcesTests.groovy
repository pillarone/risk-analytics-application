package org.pillarone.riskanalytics.application.util

import org.junit.Test
import org.pillarone.riskanalytics.core.user.UserSettings

class LocaleResourcesTests {

    @Test
    void testGetLocale_language() {
        Locale locale = LocaleResources.buildLocale(new UserSettings(language: 'fr'))
        assert Locale.FRENCH == locale
    }

    @Test
    void testGetLocale_language_and_country() {
        Locale locale = LocaleResources.buildLocale(new UserSettings(language: 'de_CH'))
        assert new Locale('de', 'CH') == locale
    }
}
