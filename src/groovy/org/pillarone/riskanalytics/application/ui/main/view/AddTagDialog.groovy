package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.comment.model.ItemListModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AddTagDialog {
    private ULCWindow parent
    ULCTableTree tree
    AbstractTableTreeModel model
    ULCDialog dialog
    ULCList tags
    ULCTextField newTag

    private ULCButton applyButton
    private ULCButton addNewButton
    private ULCButton cancelButton
    ModellingItem item

    Closure okAction
    String title
    ItemListModel tagListModel
    Dimension buttonDimension = new Dimension(120, 20)


    Closure closeAction = {event -> dialog.visible = false; dialog.dispose()}

    public AddTagDialog(ULCTableTree tree, AbstractTableTreeModel model, ModellingItem item) {
        this.tree = tree
        this.model = model
        if (!item.isLoaded())
            item.load(true)
        this.item = item

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
        List<Tag> dialogTags = getItems()
        tagListModel = new ItemListModel(dialogTags?.collect {it.name}.toArray(), dialogTags)
        tags = new ULCList(tagListModel)
        tags.name = "tagesList"
        tags.setSelectedIndices(tagListModel.getSelectedIndices(item?.getTags()?.collect {it.name}))
        tags.setVisibleRowCount(6);
        tags.setMinimumSize(new Dimension(160, 100))
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

    public List<Tag> getItems() {
        return Tag.findAllByTagType(EnumTagType.PARAMETERIZATION)
    }

    private void layoutComponents() {
        newTag.setPreferredSize(new Dimension(160, 20))
        ULCBoxPane content = new ULCBoxPane(rows: 3, columns: 2)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ULCScrollPane scrollList = new ULCScrollPane(tags)
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
            if (tagName && !Tag.findByName(tagName)) {
                Tag newTag = new Tag(name: tagName, tagType: EnumTagType.PARAMETERIZATION)
                Tag.withTransaction {
                    newTag.save()
                    tagListModel.add(tagName, newTag)
                    tagListModel.fireIntervalAdded(evt.source, tagListModel.getSize() - 1, tagListModel.getSize() - 1)
                }
            }

        }] as IActionListener)
        applyButton.addActionListener([actionPerformed: {ActionEvent evt ->
            item.setTags(tagListModel.getSelectedValues(tags.getSelectedIndices()))
            item.save()
            closeAction.call()
            DefaultMutableTableTreeNode node = tree?.selectedPath?.lastPathComponent
            model.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
        }] as IActionListener)
    }
}
