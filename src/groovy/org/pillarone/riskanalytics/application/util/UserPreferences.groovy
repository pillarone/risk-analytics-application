package org.pillarone.riskanalytics.application.util

import com.ulcjava.base.application.ClientContext
import java.util.prefs.Preferences

/**
 * @author fouad jaada
 */

public class UserPreferences {
    private Preferences userPrefs
    final static String IMPORT_DIR_KEY = "import_directory"
    final static String RESULT_DIR_KEY = "result_directory"
    final static String EXPORT_DIR_KEY = "export_directory"
    final static String DEFAULT_RESULT = "default_result"
    final static String USER_PREFERRED_LANG = "userPreferredLanguage"
    final static String ADD_FILE_DIR = "add_file_directory"

    public UserPreferences() {
        userPrefs = Preferences.userNodeForPackage(UserPreferences.class);
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

    public void setLanguage(String lang) {
        userPrefs.put(USER_PREFERRED_LANG, lang)
    }

    public String getLanguage() {
        return userPrefs.get(USER_PREFERRED_LANG, null)
    }

    public void setDefaultResult(String modelClassName, String resultName) {
        userPrefs.put(DEFAULT_RESULT + "_${modelClassName}", resultName)
    }

    public String getDefaultResult(String modelClassName) {
        userPrefs.get(DEFAULT_RESULT + "_${modelClassName}", null)
    }

    public static String getUserDirectory() {
        String dir
        String resultDir = (new UserPreferences()).getUserDirectory(UserPreferences.RESULT_DIR_KEY)
        if (resultDir != null) {
            dir = resultDir
        } else {
            String userHome = ClientContext.getSystemProperty('user.home')
            if (userHome == null) {
                userHome = System.getProperty("user.home")
            }
            dir = userHome
        }
        return dir
    }

}