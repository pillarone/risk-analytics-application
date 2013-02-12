package org.pillarone.riskanalytics.application.util.prefs.impl

import org.pillarone.riskanalytics.core.user.UserManagement

import java.util.prefs.Preferences
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences

/**
 * @author fouad jaada
 */

public class UserPreferencesImpl extends AbstractUserPreferencesImpl {
    private Preferences userPrefs

    public UserPreferencesImpl() {
        String user = UserManagement.currentUser ?: "standalone"
        userPrefs = Preferences.userNodeForPackage(UserPreferencesImpl.class).node(user);
    }

    String getUserDirectory(String key) {
        return userPrefs.get(key, System.getProperty("user.home"))
    }

    void setUserDirectory(String[] paths, String[] names) {
        if (paths && paths.length > 0 && names && names.length > 0)
            userPrefs.put(IMPORT_DIR_KEY, paths[0].substring(0, paths[0].indexOf(names[0])))
    }

    void setUserDirectory(String key, String value) {
        userPrefs.put(key, value)
    }

    public void putPropertyValue(String property, String value) {
        if (value)
            userPrefs.put(property, value)
    }

    public String getPropertyValue(String property) {
        userPrefs.get(property, null)
    }

    public String getDefaultValue(String property, String defaultValue) {
        return userPrefs.get(property, defaultValue)
    }

}