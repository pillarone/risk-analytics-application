package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.tree.ITreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.base.model.ModelNode
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model

abstract class SelectionTreeAction extends ResourceBasedAction {

    ULCTree tree
    P1RATModel model

    def SelectionTreeAction(name, tree, P1RATModel model) {
        super(name);
        this.tree = tree;
        this.model = model
        checkForIcon()
    }

    public SelectionTreeAction(String title) {
        super(title);
        checkForIcon()
    }

    private checkForIcon() {
        if (getValue(IAction.SMALL_ICON) == null) {
            putValue(IAction.SMALL_ICON, UIUtils.getIcon("clear.png"));
        }
    }

    Object getSelectedItem() {
        ITreeNode itemNode = tree.selectionPath?.lastPathComponent
        return itemNode instanceof ItemNode ? itemNode.item : null
    }

    List getSelectedObjects(Class itemClass) {
        List selectedObjects = []
        for (TreePath selectedPath in tree.selectionPaths) {
            for (Object node in selectedPath.getPath()) {
                if (node instanceof ItemGroupNode) {
                    if (node.itemClass == itemClass && selectedPath?.lastPathComponent != null) {
                        Object lastNode = selectedPath.lastPathComponent
                        if (lastNode instanceof ItemNode) {
                            selectedObjects.add(lastNode)
                        }
                    }
                }
            }
        }
        return selectedObjects
    }

    Model getSelectedModel() {
        ITreeNode itemNode = tree?.selectionPath?.lastPathComponent
        return getSelectedModel(itemNode)
    }

    Model getSelectedModel(ITreeNode itemNode) {
        ITreeNode modelNode = null
        while (modelNode == null) {
            if (itemNode instanceof ModelNode) {
                modelNode = itemNode
            } else {
                itemNode = itemNode?.parent
            }
        }
        return modelNode?.item
    }

    Class getSelectedItemGroupClass() {
        return getSelectedItemGroupNode().itemClass
    }

    ItemGroupNode getSelectedItemGroupNode() {
        ITreeNode itemNode = tree.selectionPath.lastPathComponent
        ITreeNode groupNode = null
        while (groupNode == null) {
            if (itemNode instanceof ItemGroupNode) {
                groupNode = itemNode
            } else {
                itemNode = itemNode.parent
            }
        }
        return groupNode
    }
}


















