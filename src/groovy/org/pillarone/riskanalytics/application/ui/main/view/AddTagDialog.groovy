package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel

import com.ulcjava.base.application.util.Dimension

import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.util.UIUtils

import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AddTagDialog {
    private ULCWindow parent
    ULCTableTree tree
    AbstractTableTreeModel model
    ULCDialog dialog
    TagsListView tagsListView
    ULCTextField newTag

    private ULCButton applyButton
    private ULCButton addNewButton
    private ULCButton cancelButton
    List<ModellingUIItem> modellingUIItems

    Closure okAction
    String title
    Dimension buttonDimension = new Dimension(120, 20)


    Closure closeAction = { event -> dialog.visible = false; dialog.dispose() }

    // TagsAction creates, inits it then makes this visible
    //
    public AddTagDialog(ULCTableTree tree, List<ModellingUIItem> modellingUIItems) {
        this.tree = tree
        this.model = tree.model
        this.modellingUIItems = modellingUIItems
        load()
    }

    private void load() {
        for (ModellingUIItem modellingUIItem : modellingUIItems) {
            if (!modellingUIItem.isLoaded())
                modellingUIItem.load(true)
        }
    }

    public void init() {
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        if (tree)
            this.parent = UlcUtilities.getWindowAncestor(tree)
        dialog = new ULCDialog(parent, "Edit tags dialog", true)
        dialog.name = 'AddTagDialog'                                // Beware - names may be used in tests
        tagsListView = new TagsListView(modellingUIItems*.item, parent)
        tagsListView.init()
        newTag = new ULCTextField()
        newTag.name = 'newTag'
        addNewButton = new ULCButton("Create new tag")
        addNewButton.name = "addNew"
        addNewButton.setPreferredSize(buttonDimension)
//        addNewButton.setEnabled( !newTag.getText()?.trim()?.empty ) // Ought to enable button only when user types new tag name
        applyButton = new ULCButton("Apply")
        applyButton.name = "apply"
        applyButton.setPreferredSize(buttonDimension)
        cancelButton = new ULCButton("Cancel")
        cancelButton.setPreferredSize(buttonDimension)
    }


    private void layoutComponents() {
        newTag.setPreferredSize(new Dimension(160, 20))
        ULCBoxPane content = new ULCBoxPane(rows: 3, columns: 2)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ULCScrollPane scrollList = new ULCScrollPane(tagsListView.content)
        scrollList.verticalScrollBar.blockIncrement = 180               // more reasonable scrollbar page-up/down size
        scrollList.setPreferredSize(new Dimension(160, 400))            // width, height
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, newTag)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, addNewButton)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, scrollList)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, cancelButton)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, applyButton)
        dialog.add(content)
        dialog.setLocationRelativeTo(parent)
        dialog.pack()
        dialog.resizable = true
    }

    private void attachListeners() {
        cancelButton.addActionListener([actionPerformed: { ActionEvent evt ->
            for (ModellingUIItem modellingUIItem : modellingUIItems) {
                if (modellingUIItem.item.changed) {
                    modellingUIItem.load(true)
                    modellingUIItem.item.setChanged(false)
                }
            }
            closeAction.call()
        }] as IActionListener)
        addNewButton.addActionListener([actionPerformed: { ActionEvent evt ->
            String tagName = newTag.getText()
            tagsListView.addTag(tagName)

        }] as IActionListener)
        applyButton.addActionListener([actionPerformed: { ActionEvent evt ->
            for (ModellingUIItem modellingUIItem : modellingUIItems) {
                if (modellingUIItem.item.changed) {
                    modellingUIItem.save()
                }
            }
            closeAction.call()
        }] as IActionListener)
    }
}
