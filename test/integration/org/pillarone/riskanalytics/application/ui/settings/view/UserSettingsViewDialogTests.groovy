package org.pillarone.riskanalytics.application.ui.settings.view

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import com.ulcjava.testframework.operator.ULCDialogOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCLabelOperator

import groovy.mock.interceptor.MockFor
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.user.UserManagement
import org.pillarone.riskanalytics.application.user.ApplicationUser
import org.pillarone.riskanalytics.application.ui.settings.model.LanguagesValues
import org.pillarone.riskanalytics.application.ui.settings.model.UserSettingsViewModel

import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class UserSettingsViewDialogTests extends AbstractSimpleFunctionalTest {
    UserSettingsViewDialog dialog
    MockFor clientContext
    MockFor userManagement

    ApplicationUser testUser


    protected void doStart() {
        testUser = createUser()
        ULCFrame frame = new ULCFrame("testFrame")
        frame.visible = true
        dialog = new UserSettingsViewDialog(new UserSettingsViewModel(), frame)
        dialog.visible = true

        clientContext = new MockFor(ClientContext)
        clientContext.demand.getLocale(0..1) {Locale.default }

        userManagement = new MockFor(UserManagement)
        userManagement.demand.getCurrentUser(1..10) {-> testUser}
    }


    public void testSaveLanguage() {
        userManagement.use {
            clientContext.use {
                ULCFrameOperator frameOperator = new ULCFrameOperator("testFrame")
                assertNotNull frameOperator

                ULCDialogOperator dialogOperator = new ULCDialogOperator(frameOperator, new ComponentByNameChooser("UserSettingsViewDialog"))
                assertNotNull dialogOperator

                ULCLabelOperator warningLabelOperator = new ULCLabelOperator(dialogOperator, new ComponentByNameChooser("messageLabel"))
                assertNotNull warningLabelOperator
                assertEquals "", warningLabelOperator.text

                ULCComboBoxOperator languageComboBox = new ULCComboBoxOperator(dialogOperator, new ComponentByNameChooser("languageComboBox"))
                assertNotNull languageComboBox
                languageComboBox.selectItem(LocaleResources.getString("LanguagesValues.fr"))

                assertEquals dialog.getText("notify"), warningLabelOperator.text

                ULCButtonOperator okButtonOperator = new ULCButtonOperator(dialogOperator, new ComponentByNameChooser("okButton"))
                assertNotNull okButtonOperator
                assertNotNull UserManagement.getCurrentUser()
                okButtonOperator.getFocus()
                okButtonOperator.clickMouse()

                assertEquals LanguagesValues.FR.toString(), testUser.userSettings.language
            }
        }
    }

    private ApplicationUser createUser() {
        ApplicationUser user = new ApplicationUser()
        user.username = "testUser2"
        user.lastname = "last"
        user.firstname = "first"
        user.email = "email@pillarone.com"
        user.password = "123456"
        return user
    }

}