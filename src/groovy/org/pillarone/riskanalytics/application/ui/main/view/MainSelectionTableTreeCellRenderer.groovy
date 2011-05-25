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

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MainSelectionTableTreeCellRenderer extends DefaultTableTreeCellRenderer {

    ULCTableTree tree
    RiskAnalyticsMainModel mainModel
    Map<Class, ULCPopupMenu> popupMenus = [:]
    Map<Status, ULCPopupMenu> workflowMenus = new HashMap<Status, ULCPopupMenu>()

    public MainSelectionTableTreeCellRenderer(ULCTableTree tree, RiskAnalyticsMainModel mainModel) {
        this.tree = tree
        this.mainModel = mainModel
    }


    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        setFont(node)
        IRendererComponent component = super.getTableTreeCellRendererComponent(tree, value, selected, expanded, leaf, hasFocus, node)
        ((ULCComponent) component).setComponentPopupMenu(getPopupMenu(node))
        ((ULCComponent) component).setToolTipText(((INavigationTreeNode) node).getToolTip())
        setIcon(((INavigationTreeNode) node).getIcon())
        return component

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

    private ULCPopupMenu getPopupMenu(ItemGroupNode node) {
        if (popupMenus.containsKey(node.itemClass)) return popupMenus.get(node.itemClass)
        ULCPopupMenu popupMenu = node.getPopupMenu(tree)
        popupMenus.put(node.itemClass, popupMenu)
        return popupMenu
    }

    private ULCPopupMenu getPopUpMenu(WorkflowParameterizationNode node) {
        if (workflowMenus.containsKey(node.status)) return workflowMenus.get(node.status)
        ULCPopupMenu popupMenu = node.getPopupMenu(tree)
        workflowMenus.put(node.status, popupMenu)
        return popupMenu
    }


}