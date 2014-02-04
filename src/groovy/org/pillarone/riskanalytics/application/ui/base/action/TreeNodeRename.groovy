package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.application.ui.util.ComponentUtils

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TreeNodeRename extends TreeNodeAction {

    public TreeNodeRename(def tree, ParameterViewModel model) {
        super(tree, model, "RenameNode")
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true));
    }

    protected void doAction(String newName, ParameterViewModel model, ITableTreeNode node, tree, boolean withComments) {
        if (node.component.name == newName)  {
            return
        }
        String oldPath = ComponentUtils.removeModelFromPath(node.path, model.model)
        String newPath = ComponentUtils.removeModelFromPath(node.parent.path, model.model) + ":$newName"

        Component component = node.parent.component.createDefaultSubComponent()
        component.name = newName
        model.parametrizedItem.renameComponent(oldPath, newPath, component)

    }

    public boolean isEnabled() {
        return super.isEnabled() && !model.paramterTableTreeModel.readOnly;
    }

}