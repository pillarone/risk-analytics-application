package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*
import com.ulcjava.base.application.util.HTMLUtilities

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AlertDialog {
    private ULCWindow parent
    ULCTableTree tree
    ULCDialog dialog
    ULCLabel messageLabel
    private ULCButton okButton
    private ULCButton cancelButton
    String title
    String message
    Closure closeAction = {event -> dialog.visible = false; dialog.dispose()}
    Closure okAction
    List<AbstractUIItem> selectedItems
    def nextItemToSelect
    Dimension buttonDimension = new Dimension(80, 20)

    public AlertDialog(ULCTableTree tree, List<AbstractUIItem> selectedItems, def nextItemToSelect, String title, String message, Closure okAction) {
        this.tree = tree
        this.title = title
        this.message = message
        this.okAction = okAction
        this.selectedItems = selectedItems
        this.nextItemToSelect = nextItemToSelect
    }

    public void init() {
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        if (tree)
            this.parent = UlcUtilities.getWindowAncestor(tree)
        dialog = new ULCDialog(parent, title, true)
        dialog.name = 'AlertDialog'
        messageLabel = new ULCLabel(HTMLUtilities.convertToHtml(message))
        okButton = new ULCButton(UIUtils.getText(this.class, "OK"))
        okButton.setPreferredSize(buttonDimension)
        okButton.name = "AlertDialog.ok"
        cancelButton = new ULCButton(UIUtils.getText(this.class, "Cancel"))
        cancelButton.setPreferredSize(buttonDimension)
        cancelButton.name = "AlertDialog.cancel"
    }

    private void layoutComponents() {
        ULCBoxPane content = new ULCBoxPane(rows: 2, columns: 1)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ULCBoxPane pane = new ULCBoxPane(rows: 1, columns: 3)
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, okButton)
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, ULCFiller.createHorizontalStrut(15))
        pane.add(ULCBoxPane.BOX_RIGHT_CENTER, cancelButton)
        content.add(ULCBoxPane.BOX_CENTER_EXPAND, messageLabel)
        content.add(ULCFiller.createVerticalStrut(20))
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, pane)

        dialog.add(content)
        dialog.setLocationRelativeTo(parent)
        dialog.pack()
        dialog.resizable = false
    }

    private void attachListeners() {
        cancelButton.addActionListener([actionPerformed: {ActionEvent evt -> closeAction.call()}] as IActionListener)
        okButton.addActionListener([actionPerformed: {ActionEvent evt ->
            (selectedItems) ? okAction.call([selectedItems, nextItemToSelect] as Object[]) : okAction.call()
            closeAction.call()
        }] as IActionListener)
    }

    public void show() {
        dialog.setVisible(true)
    }

}
