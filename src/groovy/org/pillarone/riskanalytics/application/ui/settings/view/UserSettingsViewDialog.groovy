package org.pillarone.riskanalytics.application.ui.settings.view

import com.ulcjava.base.application.border.ULCAbstractBorder
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.settings.model.UserSettingsViewModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.base.application.*

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */


public class UserSettingsViewDialog {

    private ULCDialog dialog
    private ULCWindow rootPane
    private ULCLabel favoriteLanguageLabel
    private ULCLabel showMessage
    private ULCComboBox languagesComboBox
    private ULCButton okButton
    UserSettingsViewModel model

    Closure okAction = {event -> model.save(); setVisible(false)  }

    public UserSettingsViewDialog(UserSettingsViewModel model, ULCWindow rootPane) {
        this.@model = model
        this.@rootPane = rootPane
        initComponents()
        initWidgets(rootPane)
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        dialog = new ULCDialog(rootPane, getText("settings"), true)
        dialog.setLocationRelativeTo(rootPane)
        dialog.size = new Dimension(400, 150)
        dialog.name = "UserSettingsViewDialog"
    }

    private void initWidgets(ULCRootPane owner) {
        favoriteLanguageLabel = new ULCLabel(getText("favoriteLanguage"))
        languagesComboBox = new ULCComboBox(model.languagesComboBoxModel)
        languagesComboBox.name = "languageComboBox"
        showMessage = new ULCLabel("")
        showMessage.foreground = Color.blue
        showMessage.name = "messageLabel"
        okButton = new ULCButton(getText("apply"))
        okButton.name = "okButton"
    }

    private void layoutComponents() {
        ULCBoxPane mainBoxPane = new ULCBoxPane(1, 2, 20, 5)
        ULCBoxPane formBoxPane = new ULCBoxPane(2, 1, 15, 5)
        ULCAbstractBorder emptyBorder = BorderFactory.createEmptyBorder(0, 10, 0, 0)

        formBoxPane.setBorder(BorderFactory.createTitledBorder(getText("languages")))
        formBoxPane.add(2, ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        favoriteLanguageLabel.setBorder(emptyBorder)
        formBoxPane.add(ULCBoxPane.BOX_LEFT_EXPAND, favoriteLanguageLabel)
        formBoxPane.add(ULCBoxPane.BOX_RIGHT_TOP, languagesComboBox)


        mainBoxPane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())


        showMessage.setBorder(emptyBorder)
        mainBoxPane.add(ULCBoxPane.BOX_EXPAND_TOP, showMessage)
        mainBoxPane.add(ULCBoxPane.BOX_EXPAND_TOP, formBoxPane)
        mainBoxPane.add(ULCBoxPane.BOX_RIGHT_TOP, okButton)
        dialog.add(mainBoxPane)

    }

    private void attachListeners() {
        okButton.addActionListener([actionPerformed: okAction] as IActionListener)
        languagesComboBox.addActionListener([actionPerformed: {
            if (model.languageChanged()) {
                showMessage.text = getText("notify")
            } else {
                showMessage.text = ""
            }
        }] as IActionListener)
    }

    public void setVisible(boolean visible) {
        dialog.visible = visible
    }

    protected String getText(String key) {
        return LocaleResources.getString("UserSettingsViewDialog." + key);
    }


}