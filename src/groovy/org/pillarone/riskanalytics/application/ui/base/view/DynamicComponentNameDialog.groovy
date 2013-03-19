package org.pillarone.riskanalytics.application.ui.base.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.*

class DynamicComponentNameDialog {

    private ULCWindow parent
    private ULCDialog dialog
    ULCTextField nameInput
    ULCCheckBox withComments
    String nameValue
    private ULCButton okButton
    private ULCButton cancelButton

    Closure okAction
    String title

    public DynamicComponentNameDialog(ULCWindow parent) {
        this.parent = parent
        initComponents()
        layoutComponents()
        attachListeners()
    }

    public DynamicComponentNameDialog(ULCWindow parent, String nameValue) {
        this.parent = parent
        this.nameValue = nameValue
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        dialog = new ULCDialog(parent, true)
        dialog.name = 'renameDialog'
        nameInput = new ULCTextField()
        nameInput.name = 'newName'
        nameInput.value = nameValue
        withComments = new ULCCheckBox("withComment")
        withComments.setSelected(true)
        withComments.setVisible(false)
        okButton = new ULCButton("Ok")
        okButton.name = 'okButton'
        okButton.enabler = nameInput
        cancelButton = new ULCButton("Cancel")

    }

    private void layoutComponents() {
        nameInput.setPreferredSize(new Dimension(200, 20))
        ULCBoxPane content = new ULCBoxPane(rows: 2, columns: 4)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel("Name:"))
        content.add(3, ULCBoxPane.BOX_EXPAND_CENTER, nameInput)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCFiller())
        content.add(ULCBoxPane.BOX_LEFT_CENTER, withComments)
        content.add(2,ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
        content.add(2,ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
        okButton.setPreferredSize(new Dimension(70, 20))
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, okButton)
        cancelButton.setPreferredSize(new Dimension(70, 20))
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, cancelButton)

        dialog.add(content)
        dialog.setLocationRelativeTo(parent)
        dialog.pack()
        dialog.resizable = false

    }

    private void attachListeners() {
        IActionListener action = [actionPerformed: {e ->
            okAction.call(); hide()
        }] as IActionListener

        nameInput.addActionListener(action)
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
}