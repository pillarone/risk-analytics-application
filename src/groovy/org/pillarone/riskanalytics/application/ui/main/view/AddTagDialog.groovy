package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.comment.model.ItemListModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AddTagDialog {
    private ULCWindow parent
    ULCTableTree tree
    AbstractTableTreeModel model
    ULCDialog dialog
    TagesListView tagesListView
    ULCTextField newTag

    private ULCButton applyButton
    private ULCButton addNewButton
    private ULCButton cancelButton
    ModellingUIItem modellingUIItem

    Closure okAction
    String title
    ItemListModel tagListModel
    Dimension buttonDimension = new Dimension(120, 20)


    Closure closeAction = {event -> dialog.visible = false; dialog.dispose()}

    public AddTagDialog(ULCTableTree tree, ModellingUIItem modellingUIItem) {
        this.tree = tree
        this.model = tree.model
        if (!modellingUIItem.isLoaded())
            modellingUIItem.load(true)
        this.modellingUIItem = modellingUIItem
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
        tagesListView = new TagesListView(modellingUIItem.item)
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
        scrollList.setPreferredSize(new Dimension(160, 100))
        content.add(ULCBoxPane.BOX_LEFT_CENTER, scrollList)
        content.add(ULCBoxPane.BOX_LEFT_TOP, applyButton)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, newTag)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, addNewButton)
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
        content.add(ULCBoxPane.BOX_LEFT_BOTTOM, UIUtils.spaceAround(cancelButton, 10, 0, 0, 0))

        dialog.add(content)
        dialog.setLocationRelativeTo(parent)
        dialog.pack()
        dialog.resizable = false
    }

    private void attachListeners() {
        cancelButton.addActionListener([actionPerformed: {ActionEvent evt -> closeAction.call()}] as IActionListener)
        addNewButton.addActionListener([actionPerformed: {ActionEvent evt ->
            String tagName = newTag.getText()
            tagesListView.addTag(tagName)

        }] as IActionListener)
        applyButton.addActionListener([actionPerformed: {ActionEvent evt ->
            if (!modellingUIItem.isLoaded())
                modellingUIItem.load(true)
            modellingUIItem.item.setTags(tagesListView.itemTages as Set)
            modellingUIItem.save()
            closeAction.call()
            DefaultMutableTableTreeNode node = tree?.selectedPath?.lastPathComponent
            tagesListView.itemTages?.each {
                model.addColumnValue(modellingUIItem.item, node, ModellingInformationTableTreeModel.TAGS, it.toString())
            }
            model.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
        }] as IActionListener)
    }
}
