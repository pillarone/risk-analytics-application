package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.core.workflow.StatusChangeService

abstract class AbstractWorkflowAction extends SelectionTreeAction {

    private StatusChangeService service = getService()

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
        try {
            Person user = UserManagement.getCurrentUser()
            return user != null && user.getAuthorities()*.authority.contains(requiredRole()) && isActionEnabled()
        } catch (Exception ex) {}
        return false
    }

    abstract protected String requiredRole()

    protected boolean isActionEnabled() {
        return true
    }

    StatusChangeService getService() {
        try {
            return StatusChangeService.getService()
        } catch (Exception ex) {}
        return null
    }


}
