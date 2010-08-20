package org.pillarone.riskanalytics.application.util

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.datatype.ULCNumberDataType
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.user.UserSettings

/**
 * This class provides properties from resource bundles using client-specific internationalization.
 * <p/>
 * Note: This class is not synchronized and should only be called from the ULC Thread.
 *
 * @author Dierk.Koenig@canoo.com
 */
class LocaleResources {

    private static final String BUNDLE_FILENAME = "org.pillarone.riskanalytics.application.applicationResources"
    private static boolean sTestMode
    private static final String LOCALE = "SESSION_LOCAL_LOCALE"

    static String getString(String key) {
        getBundle(BUNDLE_FILENAME).getString(key)
    }

    static ResourceBundle getBundle(String bundleFilename) {
        ResourceBundle.getBundle(bundleFilename, getLocale())
    }

    static Locale getLocale() {
        if (sTestMode) {
            return Locale.getDefault()
        }
        Locale locale = (Locale) UserContext.getAttribute(LOCALE)

        if (locale == null) {
            Person.withTransaction {e ->
                UserSettings userSettings = UserManagement.getCurrentUser()?.settings
                UserPreferences preferences = new UserPreferences()
                if (userSettings != null) {
                    locale = new Locale(userSettings.language)
                    UserContext.setAttribute(LOCALE, locale)
                } else if (preferences.getLanguage() != null) {
                    locale = new Locale(preferences.getLanguage())
                    UserContext.setAttribute(LOCALE, locale)
                }
            }
            if (locale == null) {
                locale = ClientContext.getLocale()
                if (locale == null) {
                    locale = new Locale("en", "US")
                }
                UserContext.setAttribute(LOCALE, locale)
            }
            Locale.setDefault(locale)
        }

        return locale
    }


    static NumberFormat getNumberFormat() {
        NumberFormat.getInstance(UIUtils.clientLocale)
    }

    static ULCNumberDataType getNumberDataType() {
        ULCNumberDataType type = new ULCNumberDataType(ClientContext.getLocale())
        if (!type) {
            type = new ULCNumberDataType()
        }
        return type
    }

    static DateFormat getDateFormat() {
        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT, SimpleDateFormat.SHORT, getLocale())
    }

    static void setTestMode() {
        sTestMode = true
    }

    static boolean getTestMode() {
        return sTestMode
    }

    static void clearTestMode() {
        sTestMode = false
    }

}
