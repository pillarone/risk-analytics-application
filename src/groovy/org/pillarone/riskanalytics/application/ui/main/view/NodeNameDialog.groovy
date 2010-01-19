package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.base.application.*

class NodeNameDialog {

    private ModellingItem item
    private ULCWindow parent
    private ULCDialog dialog
    ULCTextField nameInput
    private ULCButton okButton
    private ULCButton cancelButton

    Closure okAction
    String title

    NodeNameDialog(ULCWindow parent, ModellingItem item) {
        this.parent = parent
        this.item = item
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        dialog = new ULCDialog(parent, true)
        dialog.name = 'renameDialog'
        nameInput = new ULCTextField(item.name)
        nameInput.name = 'newName'
        okButton = new ULCButton(getText("okButton"))
        okButton.name = 'okButton'
        cancelButton = new ULCButton(getText("cancelButton"))

    }

    private void layoutComponents() {
        nameInput.setPreferredSize(new Dimension(200, 20))
        ULCBoxPane content = new ULCBoxPane(rows: 2, columns: 4)
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
        IActionListener action = [actionPerformed: {e ->
            if (!item.isLoaded()) {
                item.load()
            }

            if (isUnique(item)) {
                okAction.call(); hide()
            } else {
                I18NAlert alert = new I18NAlert(parent, "UniquesNamesRequired")
                alert.show()
            }
        }] as IActionListener

        nameInput.addActionListener(action)
        okButton.addActionListener(action)
        cancelButton.addActionListener([actionPerformed: {e -> hide()}] as IActionListener)
    }

    protected boolean isUnique(Simulation item) {
        item.template.daoClass.findByNameAndModelClassName(nameInput.text, item.modelClass.name) == null
    }

    protected boolean isUnique(ModellingItem item) {
        item.daoClass.findByNameAndModelClassName(nameInput.text, item.modelClass.name) == null
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
    protected String getText(String key) {
        return LocaleResources.getString("NodeNameDialog." + key);
    }
}