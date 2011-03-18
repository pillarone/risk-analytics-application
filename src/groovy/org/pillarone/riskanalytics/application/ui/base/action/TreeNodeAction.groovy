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

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */
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
        if (model.paramterTableTreeModel.readOnly) return
        ITableTreeNode node = tree.selectedPath.lastPathComponent
        if (!node || !ComponentUtils.isDynamicComposedSubComponentNode(node)) return;
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

    abstract protected void doAction(String newName, ParameterViewModel model, ITableTreeNode node, tree)

    protected String getPathName(ITableTreeNode node, String name) {
        String pathName = node.path
        if (pathName.startsWith(model.model.name))
            pathName = pathName.substring(pathName.indexOf(":") + 1, pathName.length())
        return pathName + ":${name}"
    }
}