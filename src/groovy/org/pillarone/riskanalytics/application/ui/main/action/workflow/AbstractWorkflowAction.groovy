package org.pillarone.riskanalytics.application.ui.main.action.workflow

import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.workflow.StatusChangeService
import org.pillarone.riskanalytics.core.workflow.Status

import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.user.Person

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel

abstract class AbstractWorkflowAction extends SelectionTreeAction {

    private StatusChangeService service = StatusChangeService.getService()

    public AbstractWorkflowAction(String name, ULCTableTree tree, P1RATModel model) {
        super(name, tree, model)
    }

    void doActionPerformed(ActionEvent event) {
        Parameterization item = getSelectedItem()
        if (!item.isLoaded()) {
            item.load()
        }
        Parameterization parameterization = service.changeStatus(item, toStatus())
        if (!item.is(parameterization)) {
            model.selectionTreeModel.addNodeForItem(parameterization)
        } else {
            ITableTreeNode paramNode = model.selectionTreeModel.findNodeForItem(model.selectionTreeModel.root, parameterization)
            model.selectionTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(paramNode) as Object[]))
        }

    }

    abstract Status toStatus()

    final boolean isEnabled() {
        Person user = UserManagement.getCurrentUser()
        return user != null && user.getAuthorities()*.authority.contains(requiredRole()) && isActionEnabled()
    }

    abstract protected String requiredRole()

    protected boolean isActionEnabled() {
        return true
    }


}
