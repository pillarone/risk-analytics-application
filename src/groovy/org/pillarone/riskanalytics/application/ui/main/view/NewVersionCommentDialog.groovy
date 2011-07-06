package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.ULCDialog
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.ULCTextArea
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.ULCScrollPane
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NewVersionCommentDialog {

    private ModellingUIItem modellingUIItem
    private ULCTableTree tree
    ULCDialog dialog
    ULCTextArea commentTextArea
    private ULCButton okButton
    private ULCButton cancelButton

    Closure okAction
    String title

    public NewVersionCommentDialog(ULCTableTree tree, ModellingUIItem modellingUIItem, Closure okAction) {
        this.tree = tree
        this.modellingUIItem = modellingUIItem
        this.okAction = okAction
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        dialog = new ULCDialog(UlcUtilities.getWindowAncestor(tree), UIUtils.getText(NewVersionCommentDialog, "addComment"), true)
        dialog.name = 'renameDialog'
        commentTextArea = new ULCTextArea(5, 45)
        commentTextArea.setMinimumSize(new Dimension(200, 160))
        commentTextArea.setMaximumSize(new Dimension(400, 160))
        commentTextArea.name = "commentTextArea"
        commentTextArea.lineWrap = true
        commentTextArea.wrapStyleWord = true
        okButton = new ULCButton(UIUtils.getText(NewVersionCommentDialog, "createNewVersion"))
        okButton.name = 'okButton'
        cancelButton = new ULCButton(UIUtils.getText(NewVersionCommentDialog, "Cancel"))

    }

    private void layoutComponents() {
        ULCBoxPane content = new ULCBoxPane(rows: 2, columns: 4)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        content.add(3, ULCBoxPane.BOX_EXPAND_CENTER, new ULCScrollPane(commentTextArea))
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
        okButton.setPreferredSize(new Dimension(160, 20))
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, okButton)
        cancelButton.setPreferredSize(new Dimension(160, 20))
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, cancelButton)

        dialog.add(content)
        dialog.setLocationRelativeTo(UlcUtilities.getWindowAncestor(tree))
        dialog.pack()
        dialog.resizable = false

    }

    public void show() {
        dialog.visible = true
    }

    public hide() {
        dialog.visible = false
    }

    private void attachListeners() {
        IActionListener action = [actionPerformed: {e ->
            if (!modellingUIItem.isLoaded()) {
                modellingUIItem.load()
            }
            okAction.call(modellingUIItem, commentTextArea.getText()); hide();
        }] as IActionListener
        okButton.addActionListener(action)
        commentTextArea.registerKeyboardAction(action, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        cancelButton.addActionListener([actionPerformed: {e -> hide()}] as IActionListener)
    }


}
