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
    TagsListView tagesListView
    ULCTextField newTag

    private ULCButton applyButton
    private ULCButton addNewButton
    private ULCButton cancelButton
    List<ModellingUIItem> modellingUIItems

    Closure okAction
    String title
    Dimension buttonDimension = new Dimension(120, 20)


    Closure closeAction = { event -> dialog.visible = false; dialog.dispose() }

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
        dialog.name = 'AddTagDialog'
        tagesListView = new TagsListView(modellingUIItems*.item)
        tagesListView.init()
        newTag = new ULCTextField()
        newTag.name = 'newTag'
        addNewButton = new ULCButton("add new")
        addNewButton.name = "addNew"
        addNewButton.setPreferredSize(buttonDimension)
        applyButton = new ULCButton("apply")
        applyButton.name = "apply"
        applyButton.setPreferredSize(buttonDimension)
        cancelButton = new ULCButton("cancel")
        cancelButton.setPreferredSize(buttonDimension)
    }


    private void layoutComponents() {
        newTag.setPreferredSize(new Dimension(160, 20))
        ULCBoxPane content = new ULCBoxPane(rows: 3, columns: 2)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ULCScrollPane scrollList = new ULCScrollPane(tagesListView.content)
        scrollList.verticalScrollBar.blockIncrement = 180  // more reasonable scrollbar page-up/down size
        scrollList.setPreferredSize(new Dimension(160, 200))
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, scrollList)
        content.add(ULCBoxPane.BOX_LEFT_TOP, applyButton)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, newTag)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, addNewButton)
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
        content.add(ULCBoxPane.BOX_LEFT_BOTTOM, UIUtils.spaceAround(cancelButton, 10, 0, 0, 0))

        dialog.add(content)
        dialog.setLocationRelativeTo(parent)
        dialog.pack()
        dialog.resizable = true
    }

    private void attachListeners() {
        cancelButton.addActionListener([actionPerformed: { ActionEvent evt ->
            for (ModellingUIItem modellingUIItem : modellingUIItems) {
                if (modellingUIItem.changed) {
                    modellingUIItem.load(true)
                    modellingUIItem.item.setChanged(false)
                }
            }
            closeAction.call()
        }] as IActionListener)
        addNewButton.addActionListener([actionPerformed: { ActionEvent evt ->
            String tagName = newTag.getText()
            tagesListView.addTag(tagName)

        }] as IActionListener)
        applyButton.addActionListener([actionPerformed: { ActionEvent evt ->
            for (ModellingUIItem modellingUIItem : modellingUIItems) {
                if (modellingUIItem.changed) {
                    modellingUIItem.save()
                }
            }
            closeAction.call()
        }] as IActionListener)
    }
}
