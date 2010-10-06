package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.apache.commons.lang.StringUtils
import org.pillarone.riskanalytics.application.ui.base.view.DynamicComponentNameDialog
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.ComponentUtils
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class TreeNodeDuplicator extends TreeNodeAction {


    public TreeNodeDuplicator(def tree, ParameterViewModel model) {
        super(tree, model, "Duplicate");
    }


    protected void doAction(String newName, ParameterViewModel model, ITableTreeNode node, tree) {
        String oldPath = getPathName(node.parent, "${node.name}")
        String newPath = getPathName(node.parent, "$newName")
        ParameterHolderFactory.duplicateParameters(model.builder.item, oldPath, newPath)
        Component component = node.parent.component.createDefaultSubComponent()
        component.name = newName
        tree.model.addComponentNode(node.parent, component)
    }

    public boolean isEnabled() {
        return super.isEnabled() && !model.paramterTableTreeModel.readOnly;
    }


}

public class TreeNodeRename extends TreeNodeAction {

    public TreeNodeRename(def tree, ParameterViewModel model) {
        super(tree, model, "RenameNode");
    }

    protected void doAction(String newName, ParameterViewModel model, ITableTreeNode node, tree) {
        String oldPath = getPathName(node.parent, "${node.name}")
        String newPath = getPathName(node.parent, "$newName")

        ParameterHolderFactory.renamePathOfParameter(model.builder.item, oldPath, newPath)
        Component component = node.parent.component.createDefaultSubComponent()
        component.name = newName
        tree.model.addComponentNode(node.parent, component)
        tree.model.removeComponentNode(node)
    }

    public boolean isEnabled() {
        return super.isEnabled() && !model.paramterTableTreeModel.readOnly;
    }

}

abstract class TreeNodeAction extends ResourceBasedAction {

    def tree
    ParameterViewModel model

    public TreeNodeAction(def tree, ParameterViewModel model, String actionName) {
        super(actionName)
        this.tree = tree
        this.model = model
    }

    private boolean validate(String name) {
        if (name.length() == 0 || !StringUtils.isAlphanumericSpace(name)) {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "IllegalSubComponentName")
            alert.show()
            return false
        }
        return true
    }

    public void doActionPerformed(ActionEvent event) {
        ITableTreeNode node = tree.selectedPath.lastPathComponent
        DynamicComponentNameDialog dialog = new DynamicComponentNameDialog(UlcUtilities.getWindowAncestor(tree), node?.displayName)
        dialog.title = UIUtils.getText(this.class, "title") + ":"
        dialog.okAction = {
            try {
                String newName = dialog.nameInput.text.trim()

                newName = ComponentUtils.getSubComponentName(newName)
                if (validate(newName)) {
                    doAction(newName, model, node, tree)
                }
            } catch (IllegalArgumentException e) {
                ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "UniqueSubComponent")
                alert.show()
            }
        }
        dialog.show()
    }

    abstract void doAction(String newName, ParameterViewModel model, ITableTreeNode node, tree)

    protected String getPathName(ITableTreeNode node, String name) {
        String pathName = node.path
        if (pathName.startsWith(model.model.name))
            pathName = pathName.substring(pathName.indexOf(":") + 1, pathName.length())
        return pathName + ":${name}"
    }
}