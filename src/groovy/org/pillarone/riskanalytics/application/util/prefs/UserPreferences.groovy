package org.pillarone.riskanalytics.application.util.prefs

import com.ulcjava.base.application.ClientContext
import java.util.prefs.Preferences

/**
 * @author fouad jaada
 */

public interface UserPreferences {
    final static String IMPORT_DIR_KEY = "import_directory"
    final static String REPORT_DIR_KEY = "report"
    final static String RESULT_DIR_KEY = "result_directory"
    final static String EXPORT_DIR_KEY = "export_directory"
    final static String DEFAULT_RESULT = "default_result"
    final static String USER_PREFERRED_LANG = "userPreferredLanguage"
    final static String ADD_FILE_DIR = "add_file_directory"
    final static String RANDOM_SEED_USER_VALUE = "randomseedUserValue"
    final static String RANDOM_SEED_USE_USER_DEFINED = "randomseedUseUserDefined"
    final static String QUANTILE_PERSPECTIVE = "quantilePerspective"

    String getUserDirectory(String key)
    void setUserDirectory(String[] paths, String[] names)
    void setUserDirectory(String key, String value)

    public void setLanguage(String lang)
    public String getLanguage()

    public void setDefaultResult(String modelClassName, String resultName)
    public String getDefaultResult(String modelClassName)

    public void putPropertyValue(String property, String value)
    public String getPropertyValue(String property)

    public String getDefaultValue(String property, String defaultValue)
}