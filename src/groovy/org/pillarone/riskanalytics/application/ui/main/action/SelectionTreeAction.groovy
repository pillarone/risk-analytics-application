package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.base.model.ModelNode
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement

abstract class SelectionTreeAction extends ResourceBasedAction {
    private static Log LOG = LogFactory.getLog(SelectionTreeAction)

    ULCTableTree tree
    RiskAnalyticsMainModel model

    def SelectionTreeAction(String name, ULCTableTree tree, RiskAnalyticsMainModel model) {
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
        if (getValue(SMALL_ICON) == null) {
            putValue(SMALL_ICON, UIUtils.getIcon("clear.png"));
        }
    }

    Object getSelectedItem() {
        DefaultMutableTableTreeNode itemNode = tree?.selectedPath?.lastPathComponent
        return itemNode instanceof ItemNode ? itemNode.itemNodeUIItem.item : null
    }

    ModellingUIItem getSelectedUIItem() {
        DefaultMutableTableTreeNode itemNode = tree?.selectedPath?.lastPathComponent as DefaultMutableTableTreeNode
        return itemNode instanceof ItemNode ? itemNode.itemNodeUIItem : null
    }

    List<AbstractUIItem> getSelectedUIItems() {
        List selectedObjects = []
        for (TreePath selectedPath in tree.selectedPaths) {
            DefaultMutableTableTreeNode itemNode = selectedPath.lastPathComponent
            AbstractUIItem abstractUIItem = itemNode instanceof ItemNode ? itemNode.itemNodeUIItem : null
            if (abstractUIItem != null) {
                selectedObjects << abstractUIItem
            }
        }
        return selectedObjects
    }

    List<ItemNode> getReportingModellingNodes() {
        List selectedObjects = []
        for (TreePath selectedPath in tree.selectedPaths) {
            DefaultMutableTableTreeNode itemNode = selectedPath.lastPathComponent
            if (itemNode instanceof IReportableNode) {
                selectedObjects << itemNode
            }
        }
        return selectedObjects
    }

    List<ItemNode> getSelectedObjects(Class itemClass) {
        List selectedObjects = []
        for (TreePath selectedPath in tree.selectedPaths) {
            for (Object node in selectedPath.path) {
                if (node instanceof ItemGroupNode) {
                    if (node.itemClass == itemClass && selectedPath?.lastPathComponent != null) {
                        Object lastNode = selectedPath.lastPathComponent
                        if (lastNode instanceof ItemNode) {
                            selectedObjects.add(lastNode)
                            break
                        }
                    }
                }
            }
        }
        return selectedObjects
    }

    //TODO remove this in future as it loops unnecessarily, prefer the Simpler version below
    @Deprecated
    List getAllSelectedObjects() {
        List selectedObjects = []
        for (TreePath selectedPath in tree.selectedPaths) {
            for (Object node in selectedPath.path) {
                if (node instanceof ItemGroupNode) {
                    if (selectedPath?.lastPathComponent != null) {
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

    // TODO TEST THIS I think this is enough, simpler and clearer than above:
    //
    List<ItemNode> getAllSelectedObjectsSimpler() {
        List selectedObjects = []
        for (TreePath selectedPath in tree.selectedPaths) {
            Object lastNode = selectedPath?.lastPathComponent
            if (lastNode != null && lastNode instanceof ItemNode) {
                selectedObjects.add(lastNode)
            }
        }
        return selectedObjects
    }

    Model getSelectedModel() {
        DefaultMutableTableTreeNode itemNode = tree?.selectedPath?.lastPathComponent
        return getSelectedModel(itemNode)
    }

    // TODO This code is begging to be simplified.
    Model getSelectedModel(DefaultMutableTableTreeNode itemNode) {
        if (itemNode == null) {
            return null
        }
        ModelNode modelNode = null
        def parent = itemNode
        while (modelNode == null && parent?.parent) {
            if (parent instanceof ModelNode) {
                modelNode = parent
            } else {
                parent = parent?.parent
            }
        }
        return modelNode?.model
    }

    Class getSelectedItemGroupClass() {
        return selectedItemGroupNode.itemClass
    }

    ItemGroupNode getSelectedItemGroupNode() {
        ITableTreeNode itemNode = tree.selectedPath.lastPathComponent
        ITableTreeNode groupNode = null
        while (groupNode == null) {
            if (itemNode instanceof ItemGroupNode) {
                groupNode = itemNode
            } else {
                itemNode = itemNode.parent
            }
        }
        return groupNode
    }

    boolean isEnabled() {
        return super.isEnabled() && accessAllowed()
    }

    // If there is an item owner s/he can forbid someone else (F. Paul Wilson readers: KYFHO).
    // (Each action can decide what to do with this information.)
    //
    protected boolean itemOwnerCanVetoCurrentUser( Person itemOwner ){

        if( itemOwner == null ){
            return false
        }

        //We have an owner, check if current user is different
        //
        Person currentUser = UserManagement.getCurrentUser()

        if( currentUser == null ){
            LOG.info("Current user null, owner (${itemOwner.username} can veto actions")
            return true
        }

        if( ! itemOwner.username.equals(currentUser.username) ){
            LOG.info("Current user ${currentUser.username}, owner (${itemOwner.username} can veto actions")
            return true
        }

        return false
    }

    final boolean accessAllowed() {
        if (UserContext.standAlone) {
            return true
        }
        try {
            List actionAllowedRoles = allowedRoles() //restricted actions supply this
            if (!actionAllowedRoles || actionAllowedRoles.size() == 0) {
                return true
            }

            String actionName = this.getClass().getSimpleName();

            Person user = UserManagement.getCurrentUser()
            if (user == null) {
                LOG.warn("User NULL - action ${actionName} denied.")
                return false
            }

            if (user.getAuthorities()*.authority.any { actionAllowedRoles.contains(it) }) {
                return true;
            }

            // As code decides whether to enable menus on selected items.
            // lets not spam the logfile every time - level debug is enough.
            LOG.debug("Action ${actionName} denied to ${user.username} as lacks these roles (hint: table person_authority): " + actionAllowedRoles)

        } catch (Exception ex) {
            LOG.error("Error in roles lookup", ex)
        }
        return false
    }

    protected List allowedRoles() {
        return []
    }

    Model getModelInstance(ModellingItem item) {
        Model selectedModelInstance = item.modelClass.newInstance()
        selectedModelInstance.init()
        return selectedModelInstance
    }

    @Override
    String toString() {
        int num = 0;
        if (tree != null && tree.selectedPaths != null) {
            num = tree.selectedPaths.length;
        }
        return "Selected paths ($num): ${tree?.selectedPaths}"
    }
}


















