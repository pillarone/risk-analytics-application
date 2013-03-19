package org.pillarone.riskanalytics.application.util.prefs

import grails.util.Environment
import org.pillarone.riskanalytics.application.util.prefs.impl.UserPreferencesImpl
import org.pillarone.riskanalytics.application.util.prefs.impl.MockUserPreferences

/**
 * bzetterstrom
 */
public class UserPreferencesFactory {
    public static UserPreferences getUserPreferences() {
        if (Environment.getCurrent() == Environment.TEST) {
            return MockUserPreferences.INSTANCE;
        } else {
            return new UserPreferencesImpl()
        }
    }
}
