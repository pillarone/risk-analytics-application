package org.pillarone.riskanalytics.application.ui.settings.model

import org.pillarone.riskanalytics.application.ui.base.model.EnumI18NComboBoxModel
import org.pillarone.riskanalytics.application.util.UserPreferences
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.user.UserSettings

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class UserSettingsViewModel {
    EnumI18NComboBoxModel languagesComboBoxModel
    private UserPreferences userPreferences


    public UserSettingsViewModel() {
        UserSettings userSettings = UserManagement.getCurrentUser()?.settings
        if (userSettings == null) {
            languagesComboBoxModel = new EnumI18NComboBoxModel(LanguagesValues.values() as Object[])
        } else {
            languagesComboBoxModel = new EnumI18NComboBoxModel(LanguagesValues.values() as Object[], userSettings.language)
        }
        userPreferences = new UserPreferences();
    }

    public void save() {
        Person user = UserManagement.getCurrentUser()
        if (user != null) {
            if (user.settings == null) {
                user.settings = new UserSettings()
            }
            user.settings.language = languagesComboBoxModel.selectedEnum.toString()
            user.save()
        } else {
            userPreferences.setLanguage(languagesComboBoxModel.selectedEnum.toString())
        }

    }

    public boolean languageChanged() {
        Person.withTransaction {e ->
            UserSettings userSettings = UserManagement.getCurrentUser()?.settings
            return userSettings?.language != languagesComboBoxModel.selectedEnum.toString()
        }
    }
}


enum LanguagesValues {
    DE("de"), EN("en"), FR("fr")

    private String displayName

    private LanguagesValues(String displayName) {
        this.@displayName = displayName
    }

    public String toString() {
        return displayName
    }

}