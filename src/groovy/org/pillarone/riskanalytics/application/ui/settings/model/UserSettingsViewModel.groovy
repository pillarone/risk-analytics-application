package org.pillarone.riskanalytics.application.ui.settings.model

import org.pillarone.riskanalytics.application.user.UserManagement
import org.pillarone.riskanalytics.application.user.ApplicationUser
import org.pillarone.riskanalytics.application.user.UserSettings
import org.pillarone.riskanalytics.application.ui.base.model.EnumI18NComboBoxModel

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class UserSettingsViewModel {
    EnumI18NComboBoxModel languagesComboBoxModel

    public UserSettingsViewModel() {
        ApplicationUser.withTransaction {e ->
            UserSettings userSettings = UserManagement.getCurrentUser()?.userSettings
            if (userSettings == null) {
                languagesComboBoxModel = new EnumI18NComboBoxModel(LanguagesValues.values() as Object[])
            } else {
                languagesComboBoxModel = new EnumI18NComboBoxModel(LanguagesValues.values() as Object[], userSettings.language)
            }
        }
    }

    public void save() {
        ApplicationUser.withTransaction {e ->
            ApplicationUser user = UserManagement.getCurrentUser()
            if (user != null) {
                if (user.userSettings == null) {
                    user.userSettings = new UserSettings()
                }
                user.userSettings.language = languagesComboBoxModel.selectedEnum.toString()
                user.save()
            }

        }
    }

    public boolean languageChanged() {
        ApplicationUser.withTransaction {e ->
            UserSettings userSettings = UserManagement.getCurrentUser()?.userSettings
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