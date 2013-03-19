package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.core.components.Component
import com.ulcjava.base.application.ULCWindow
import org.pillarone.riskanalytics.application.ui.base.view.DynamicComponentNameDialog
import org.pillarone.riskanalytics.application.ui.util.ComponentUtils
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.DynamicComposedComponentTableTreeNode

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class TreeNodeDuplicator extends TreeNodeAction {


    public TreeNodeDuplicator(def tree, ParameterViewModel model) {
        super(tree, model, "Duplicate");
    }


    protected void doAction(String newName, ParameterViewModel model, ITableTreeNode node, tree, boolean withComments) {
        if (model.paramterTableTreeModel.readOnly) return
        if (node instanceof ComponentTableTreeNode) {
            ITableTreeNode parent = node.parent
            if (parent instanceof DynamicComposedComponentTableTreeNode) {
                String oldPath = ComponentUtils.removeModelFromPath(node.path, model.model)
                String newPath = ComponentUtils.removeModelFromPath(node.parent.path, model.model) + ":$newName"

                Component component = node.parent.component.createDefaultSubComponent()
                component.name = newName
                model.parametrizedItem.copyComponent(oldPath, newPath, component, withComments)
            }
        }
    }

    public boolean isEnabled() {
        return super.isEnabled() && !model.paramterTableTreeModel.readOnly;
    }

    @Override
    public DynamicComponentNameDialog getInputNameDialog(ULCWindow parent, String displayName) {
        DynamicComponentNameDialog dialog = new DynamicComponentNameDialog(parent, displayName)
        dialog.withComments.setVisible(true)
        return dialog
    }


}