package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.base.application.*

class DefaultParameterizationDialog {

    private ULCWindow parent
    private ULCDialog dialog
    ULCTextField nameInput
    ULCSpinner periodCount
    private ULCButton okButton
    private ULCButton cancelButton

    Closure okAction
    String title

    DefaultParameterizationDialog(ULCWindow parent, boolean hasOneParameterColumnOnly) {
        this.parent = parent
        initComponents()
        layoutComponents(hasOneParameterColumnOnly)
        attachListeners()
    }

    private void initComponents() {
        dialog = new ULCDialog(parent, true)
        dialog.name = 'renameDialog'
        nameInput = new ULCTextField()
        nameInput.name = 'newName'
        okButton = new ULCButton(getText("okButton"))
        okButton.name = 'okButton'
        okButton.enabled = false
        okButton.enabler = nameInput
        cancelButton = new ULCButton(getText("cancelButton"))

        periodCount = new ULCSpinner(new ULCSpinnerNumberModel(1 as Number, 1 as Number, null, 1 as Number))

    }

    private void layoutComponents(boolean hasOneParameterColumnOnly) {
        nameInput.setPreferredSize(new Dimension(200, 20))
        ULCBoxPane content = new ULCBoxPane(rows: 3, columns: 4)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)

        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("name") + ":"))
        content.add(3, ULCBoxPane.BOX_EXPAND_CENTER, nameInput)

        if (!hasOneParameterColumnOnly) {
            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("periods") + ":"))
            content.add(3, ULCBoxPane.BOX_EXPAND_CENTER, periodCount)
        }

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
        IActionListener action = [actionPerformed: {e -> okAction.call()}] as IActionListener
        dialog.setDefaultButton okButton
        okButton.addActionListener(action)
        cancelButton.addActionListener([actionPerformed: {e -> hide()}] as IActionListener)
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
        return LocaleResources.getString("DefaultParameterizationDialog." + key);
    }
}