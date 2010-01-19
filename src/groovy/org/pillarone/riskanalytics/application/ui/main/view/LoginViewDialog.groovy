package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.user.UserManagement
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.main.action.ExitAction

public class LoginViewDialog {

    private ULCLabel usernameLabel
    private ULCLabel passwordLabel
    private ULCLabel invalidUserPasswordLabel
    private ULCTextField usernameField
    private ULCPasswordField passwordField
    private ULCButton okButton
    private ULCButton cancelButton
    ULCBoxPane content

    Closure closeFrame




    public LoginViewDialog(Closure closeFrame) {
        this.@closeFrame = closeFrame
        initComponents()
        layoutComponents()
        attachListeners()
    }


    private void initComponents() {
        content = new ULCBoxPane(2, 4, 10, 5)

        usernameLabel = new ULCLabel(getText("username"))
        passwordLabel = new ULCLabel(getText("password"))
        invalidUserPasswordLabel = new ULCLabel(getText("failed"))//LocaleResources.getString("InvalidUserPassword"))
        invalidUserPasswordLabel.setForeground(Color.red)
        invalidUserPasswordLabel.setVisible(false)

        usernameField = new ULCTextField("")
        usernameField.setColumns(20)
        usernameLabel.setLabelFor(usernameField)  // good style ;-)

        passwordField = new ULCPasswordField("")
        passwordField.setColumns(20)
        passwordLabel.setLabelFor(passwordField)  // good style ;-)

        okButton = new ULCButton(getText("logIn"))
        cancelButton = new ULCButton(getText("cancel"))

        //init for test
        usernameField.setValue("testUser")
        passwordField.setValue("123456")
        // end init for test


    }


    private void layoutComponents() {

        content.setBorder(BorderFactory.createEtchedBorder())
        content.add(2, ULCBoxPane.BOX_LEFT_TOP, invalidUserPasswordLabel)
        content.add(ULCBoxPane.BOX_LEFT_TOP, usernameLabel)
        content.add(ULCBoxPane.BOX_RIGHT_TOP, usernameField)
        content.add(ULCBoxPane.BOX_LEFT_TOP, passwordLabel)
        content.add(ULCBoxPane.BOX_RIGHT_TOP, passwordField)
        content.add(ULCBoxPane.BOX_RIGHT_TOP, cancelButton)
        content.add(ULCBoxPane.BOX_LEFT_TOP, okButton)

        //content.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, WidgetFactory.createHorizontalFiller());

        ULCBorderLayoutPane formBorderPane = new ULCBorderLayoutPane()
        formBorderPane.add(content, ULCBorderLayoutPane.WEST);

    }

    private void attachListeners() {
        Closure okAction = {event -> checkLogin()}
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        passwordField.registerKeyboardAction([actionPerformed: okAction] as IActionListener, enter, ULCComponent.WHEN_FOCUSED);
        usernameField.registerKeyboardAction([actionPerformed: okAction] as IActionListener, enter, ULCComponent.WHEN_FOCUSED);

        cancelButton.addActionListener([actionPerformed: {event -> ExitAction.terminate()}] as IActionListener)
        okButton.addActionListener([actionPerformed: okAction] as IActionListener)
    }


    private void checkLogin() {
        UserManagement.login(usernameField.getValue(), passwordField.getValue()) ? closeFrame.call() : showInvalidUserText(true)
    }

    public void showInvalidUserText(boolean visible) {
        invalidUserPasswordLabel.setVisible(visible);
    }


    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("LoginViewDialog." + key);
    }


}
