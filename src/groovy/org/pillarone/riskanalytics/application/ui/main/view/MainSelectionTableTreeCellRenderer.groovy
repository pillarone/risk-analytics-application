package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.ui.base.model.INavigationTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.ui.parameterization.model.WorkflowParameterizationNode
import org.pillarone.riskanalytics.application.ui.resource.model.ResourceNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRunNode

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MainSelectionTableTreeCellRenderer extends DefaultTableTreeCellRenderer {

    ULCTableTree tree
    RiskAnalyticsMainModel mainModel
    Map<Class, ULCPopupMenu> popupMenus = [:]
    Map<Status, ULCPopupMenu> workflowMenus = new HashMap<Status, ULCPopupMenu>()
    Map<Status, ULCPopupMenu> resourceWorkflowMenus = new HashMap<Status, ULCPopupMenu>()

    public MainSelectionTableTreeCellRenderer(ULCTableTree tree, RiskAnalyticsMainModel mainModel) {
        this.tree = tree
        this.mainModel = mainModel
    }


    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        setFont(node)
        IRendererComponent component = super.getTableTreeCellRendererComponent(tree, value, selected, expanded, leaf, hasFocus, node)
        try {
            renderComponent((ULCComponent) component, node)
        } catch (Exception e) {
            throw new RuntimeException("Failed to render: " + e.getMessage(), e)
        }
        return component

    }

    private void renderComponent(ULCComponent component, INavigationTreeNode node) {
        component.setComponentPopupMenu(getPopupMenu(node))
        component.setToolTipText(node.getToolTip())
        setIcon(node.getIcon())
    }

    private void renderComponent(ULCComponent component, Object node) {

    }

    void setFont(ParameterizationNode node) {
        if (node.parent && !(node.parent instanceof SimulationNode)) {
            setFont(node.getFont(getFont().getName(), getFont().getSize()))
            setForeground(!node.valid ? Color.gray : null)
        }
    }

    void setFont(def node) {
        setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()))
        setForeground(null)
    }

    private ULCPopupMenu getPopupMenu(Object node) {
        if (popupMenus.containsKey(node.class)) return popupMenus.get(node.class)
        ULCPopupMenu popupMenu = ((INavigationTreeNode) node).getPopupMenu(tree)
        popupMenus.put(node.class, popupMenu)
        return popupMenu
    }

    private ULCPopupMenu getPopupMenu(SimulationNode node) {
        return ((INavigationTreeNode) node).getPopupMenu(tree)
    }

    private ULCPopupMenu getPopupMenu(ItemGroupNode node) {
        if (popupMenus.containsKey(node.itemClass)) return popupMenus.get(node.itemClass)
        ULCPopupMenu popupMenu = node.getPopupMenu(tree)
        popupMenus.put(node.itemClass, popupMenu)
        return popupMenu
    }

    private ULCPopupMenu getPopupMenu(BatchRunNode node) {
        return node.getPopupMenu(tree)
    }

    private ULCPopupMenu getPopupMenu(WorkflowParameterizationNode node) {
        Status status = node.getStatus();
        if (workflowMenus.containsKey(status)) return workflowMenus.get(status)
        ULCPopupMenu popupMenu = node.getPopupMenu(tree)
        workflowMenus.put(status, popupMenu)
        return popupMenu
    }

    private ULCPopupMenu getPopupMenu(ResourceNode node) {
        if (resourceWorkflowMenus.containsKey(node.status)) return resourceWorkflowMenus.get(node.status)
        ULCPopupMenu popupMenu = node.getPopupMenu(tree)
        resourceWorkflowMenus.put(node.status, popupMenu)
        return popupMenu
    }

    //Leave caching of the popup to ParameterizationNode, need different menus for different model versions
    private ULCPopupMenu getPopupMenu(ParameterizationNode node) {
        return node.getPopupMenu(tree)
    }



}