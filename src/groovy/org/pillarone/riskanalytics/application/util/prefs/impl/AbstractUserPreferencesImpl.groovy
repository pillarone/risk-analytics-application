package org.pillarone.riskanalytics.application.util.prefs.impl

import org.pillarone.riskanalytics.application.util.prefs.UserPreferences

/**
 * @author bzetterstrom
 */
public abstract class AbstractUserPreferencesImpl implements UserPreferences {
    public void setLanguage(String lang) {
        putPropertyValue(USER_PREFERRED_LANG, lang)
    }

    public String getLanguage() {
        getDefaultValue(USER_PREFERRED_LANG, null)
    }

    public void setDefaultResult(String modelClassName, String resultName) {
        putPropertyValue(DEFAULT_RESULT + "_${modelClassName}", resultName)
    }

    public String getDefaultResult(String modelClassName) {
        getDefaultValue(DEFAULT_RESULT + "_${modelClassName}", null)
    }

}
