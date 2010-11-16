package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TreeNodeRename extends TreeNodeAction {

    public TreeNodeRename(def tree, ParameterViewModel model) {
        super(tree, model, "RenameNode")
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true));
    }

    protected void doAction(String newName, ParameterViewModel model, ITableTreeNode node, tree) {
        String oldPath = getPathName(node.parent, "${node.name}")
        String newPath = getPathName(node.parent, "$newName")

        def oldComments = model?.item?.comments?.findAll {it.path.startsWith(model.model.name + ":" + oldPath)}
        oldComments.each {Comment comment ->
            Comment newComment = comment.clone()
            newComment.path = comment.path.replace(oldPath, newPath)
            model.item.removeComment(comment)
            model.item.addComment(newComment)
        }

        List<String> modifiedReferencePaths = ParameterHolderFactory.renamePathOfParameter(model.builder.item, oldPath, newPath)
        Component component = node.parent.component.createDefaultSubComponent()
        component.name = newName
        tree.model.addComponentNode(node.parent, component)
        tree.model.removeComponentNode(node)
        // notify all nodes referencing the renamed component
        for (String path: modifiedReferencePaths) {
            TreePath treePath = new TreePath(DefaultTableTreeModel.getPathToRoot(model.findNodeForPath(path)) as Object[])
            model.paramterTableTreeModel.nodeChanged(treePath)
        }
    }

    public boolean isEnabled() {
        return super.isEnabled() && !model.paramterTableTreeModel.readOnly;
    }

}