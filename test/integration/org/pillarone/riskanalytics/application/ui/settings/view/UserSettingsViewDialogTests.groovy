package org.pillarone.riskanalytics.application.ui.settings.view

import com.ulcjava.base.application.ULCFrame
import groovy.mock.interceptor.MockFor
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.ui.settings.model.UserSettingsViewModel
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserSettings

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class UserSettingsViewDialogTests extends AbstractSimpleFunctionalTest {
    UserSettingsViewDialog dialog
    MockFor clientContext
    MockFor userManagement

    Person testUser


    protected void doStart() {
        testUser = createUser()
        ULCFrame frame = new ULCFrame("testFrame")
        frame.visible = true
        dialog = new UserSettingsViewDialog(new UserSettingsViewModel(), frame)
        dialog.visible = true

//        clientContext = new MockFor(com.ulcjava.base.application.ClientContext)
//        clientContext.demand.getLocale(0..1) {Locale.default }

        userManagement = new MockFor(org.pillarone.riskanalytics.core.user.UserManagement)
        userManagement.demand.getCurrentUser(1..6) {->
            testUser
        }
    }

    //TODO: test fails with Session stop expected:<false> but was:<true>in com.ulcjava.testframework.AbstractTestCase.tearDown
    /* public void testSaveLanguage() {
        userManagement.use {
//            clientContext.use {
                ULCFrameOperator mainFrameOperator = new ULCFrameOperator("testFrame")
                assertNotNull mainFrameOperator

                ULCDialogOperator dialogOperator = new ULCDialogOperator(mainFrameOperator, new ComponentByNameChooser("UserSettingsViewDialog"))
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
                assertNotNull org.pillarone.riskanalytics.application.user.UserContext.getCurrentUser()
                okButtonOperator.getFocus()
                okButtonOperator.clickMouse()

                assertEquals LanguagesValues.FR.toString(), testUser.settings.language
//            }
        }
    }*/

    private Person createUser() {
        Person user = new Person()
        user.username = "testUser2"
        user.password = "123456"
        user.settings = new UserSettings(language: "en")
        return user
    }

    @Override
    protected void tearDown() {
        super.tearDown() //TODO: IntegrationTestMixin does not work when this method does not exist
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp() //TODO: IntegrationTestMixin does not work when this method does not exist
    }
}