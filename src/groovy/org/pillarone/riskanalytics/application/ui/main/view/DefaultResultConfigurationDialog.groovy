package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.ULCDialog
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.ULCFiller


class DefaultResultConfigurationDialog {

    private ULCWindow parent
    private ULCDialog dialog
    ULCTextField nameInput
    private ULCButton okButton
    private ULCButton cancelButton

    Closure okAction
    String title

    DefaultResultConfigurationDialog(ULCWindow parent) {
        this.parent = parent
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        dialog = new ULCDialog(parent, true)
        dialog.name = 'createDefaultResultConfiguration'
        nameInput = new ULCTextField()
        nameInput.name = 'newName'
        okButton = new ULCButton(getText("okButton"))
        okButton.name = 'okButton'
        okButton.enabled = false
        okButton.enabler = nameInput
        cancelButton = new ULCButton(getText("cancelButton"))
    }

    private void layoutComponents() {
        nameInput.setPreferredSize(new Dimension(200, 20))
        ULCBoxPane content = new ULCBoxPane(rows: 3, columns: 4)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)

        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("name") + ":"))
        content.add(3, ULCBoxPane.BOX_EXPAND_CENTER, nameInput)

        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
        okButton.setPreferredSize(new Dimension(120, 20))
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, okButton)
        cancelButton.setPreferredSize(new Dimension(120, 20))
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, cancelButton)

        dialog.add(content)
        dialog.setLocationRelativeTo(parent)
        dialog.pack()
        dialog.resizable = false

    }

    private void attachListeners() {
        IActionListener action = [actionPerformed: { e -> okAction.call() }] as IActionListener
        nameInput.addActionListener(action)
        okButton.addActionListener(action)
        cancelButton.addActionListener([actionPerformed: { e -> hide() }] as IActionListener)
    }


    public void show() {
        dialog.title = title
        dialog.visible = true
    }

    public hide() {
        dialog.visible = false
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    public String getText(String key) {
        return LocaleResources.getString("DefaultResultConfigurationDialog." + key);
    }
}
