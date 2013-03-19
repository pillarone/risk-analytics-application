package org.pillarone.riskanalytics.application.util.prefs.impl

import org.pillarone.riskanalytics.core.FileConstants
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log

/**
 * bzetterstrom
 */
public class MockUserPreferences extends AbstractUserPreferencesImpl {
    private static final String TEST_USER_DIR = FileConstants.TEMP_FILE_DIRECTORY + "/" + "testuserdir"

    private Map<String, String> fakeUserPrefs = new HashMap<String, String>()

    public static INSTANCE = new MockUserPreferences()

    private MockUserPreferences() {
    }

    Log LOG = LogFactory.getLog(MockUserPreferences)

    String getUserDirectory(String key) {
        return TEST_USER_DIR + "/" + key
    }

    void setUserDirectory(String key, String value) {
        LOG.info "Running tests -- Attempt to set user directory ${key} to ${value} was ignored";
    }

    void setUserDirectory(String[] paths, String[] names) {
        LOG.info "Running tests -- Attempt to set user directory to ${paths.join(', ')}  / ${names.join(', ')} was ignored";
    }

    void putPropertyValue(String property, String value) {
        fakeUserPrefs.put(property, value)
    }

    String getPropertyValue(String property) {
        return fakeUserPrefs.get(property)
    }

    String getDefaultValue(String property, String defaultValue) {
        def value = getPropertyValue(property)
        return value != null ? value : defaultValue
    }

    void clearFakePreferences() {
      fakeUserPrefs.clear()
    }
}
