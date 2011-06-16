package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.ui.util.CommentUtils
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class TreeNodeDuplicator extends TreeNodeAction {


    public TreeNodeDuplicator(def tree, ParameterViewModel model) {
        super(tree, model, "Duplicate");
    }


    protected void doAction(String newName, ParameterViewModel model, ITableTreeNode node, tree) {
        if (model.paramterTableTreeModel.readOnly) return
        String oldPath = getPathName(node.parent, "${node.name}")
        String newPath = getPathName(node.parent, "$newName")
        ParameterHolderFactory.duplicateParameters(model.builder.item, oldPath, newPath)
        List commentedPaths = CommentUtils.duplicateComments(model.builder.item, oldPath, newPath)
        Component component = node.parent.component.createDefaultSubComponent()
        component.name = newName
        tree.model.addComponentNode(node.parent, component)
        // notify all nodes referencing the renamed component
        for (Map pathCommentMap: commentedPaths) {
            SimpleTableTreeNode duplicatedNode = model.findNodeForPath((String) pathCommentMap["path"])
            if (duplicatedNode) {
                duplicatedNode.comments << (Comment) pathCommentMap["comment"]
                TreePath treePath = new TreePath(DefaultTableTreeModel.getPathToRoot(duplicatedNode) as Object[])
                model.paramterTableTreeModel.nodeChanged(treePath)
            }

        }
    }



    public boolean isEnabled() {
        return super.isEnabled() && !model.paramterTableTreeModel.readOnly;
    }
}