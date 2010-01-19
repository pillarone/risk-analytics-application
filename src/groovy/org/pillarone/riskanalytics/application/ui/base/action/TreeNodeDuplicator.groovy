package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.apache.commons.lang.StringUtils
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.view.ComponentUtils
import org.pillarone.riskanalytics.application.ui.base.view.DynamicComponentNameDialog
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.components.Component

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class TreeNodeDuplicator extends ResourceBasedAction {

    def tree


    public TreeNodeDuplicator(def tree) {
        super("Duplicate");
        this.tree = tree
    }


    public void doActionPerformed(ActionEvent event) {
        ITableTreeNode node = tree.selectedPath.lastPathComponent
        Component component = node.component.clone()
        component.name = component.name + "Copy"
        tree.model.addComponentNode(node.parent, component)
    }


}

public class TreeNodeRename extends ResourceBasedAction {

    def tree

    public TreeNodeRename(def tree) {
        super("RenameNode");
        this.tree = tree
    }


    public void doActionPerformed(ActionEvent event) {
        DynamicComponentNameDialog dialog = new DynamicComponentNameDialog(UlcUtilities.getWindowAncestor(tree))
        dialog.title = UIUtils.getText(this.class, "title") + ":"
        dialog.okAction = {
            try {
                String newName = dialog.nameInput.text.trim()
                ITableTreeNode node = tree.selectedPath.lastPathComponent
                newName = ComponentUtils.getSubComponentName(newName)
                if (validate(newName)) {
                    Component component = node.component.clone()
                    component.name = newName
                    tree.model.addComponentNode(node.parent, component)
                    tree.model.removeComponentNode(node)
                }
            } catch (IllegalArgumentException e) {
                ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "UniqueSubComponent")
                alert.show()
            }
        }
        dialog.show()
    }

    private boolean validate(String name) {
        if (name.length() == 0 || !StringUtils.isAlphanumericSpace(name)) {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "IllegalSubComponentName")
            alert.show()
            return false
        }
        return true
    }


}