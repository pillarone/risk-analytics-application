package org.pillarone.riskanalytics.application.ui.settings.model

import org.pillarone.riskanalytics.application.ui.base.model.EnumI18NComboBoxModel
import org.pillarone.riskanalytics.application.util.UserPreferences
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.user.UserSettings
import org.pillarone.riskanalytics.application.UserContext

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class UserSettingsViewModel {
    EnumI18NComboBoxModel languagesComboBoxModel
    private UserPreferences userPreferences


    public UserSettingsViewModel() {
        UserSettings userSettings = UserContext.getCurrentUser()?.settings
        userPreferences = new UserPreferences();
        if (userSettings == null) {
            languagesComboBoxModel = new EnumI18NComboBoxModel(LanguagesValues.values() as Object[])
            if (userPreferences.getLanguage())
                languagesComboBoxModel.setSelectedEnum(userPreferences.getLanguage())
        } else {
            languagesComboBoxModel = new EnumI18NComboBoxModel(LanguagesValues.values() as Object[], userSettings.language)
        }

    }

    public void save() {
        Person user = UserContext.getCurrentUser()
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
            UserSettings userSettings = UserContext.getCurrentUser()?.settings
            return userSettings?.language != languagesComboBoxModel.selectedEnum.toString()
        }
    }
}


enum LanguagesValues {
    DE("de"), de_CH("de_CH"), EN("en"), FR("fr"), fr_CH("fr_CH")

    private String displayName

    private LanguagesValues(String displayName) {
        this.@displayName = displayName
    }

    public String toString() {
        return displayName
    }

}