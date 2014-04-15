package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.ui.base.model.INavigationTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRunNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.resource.model.ResourceNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.core.workflow.Status

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MainSelectionTableTreeCellRenderer extends DefaultTableTreeCellRenderer {

    // TODO frahman these maps look likely suspects for our joy with menus esp report menus
    ULCTableTree tree
    RiskAnalyticsMainModel mainModel
    Map<Class, ULCPopupMenu> popupMenus = [:]
    Map<Class, ULCPopupMenu> paramNodePopupMenus = [:]
    Map<Class, ULCPopupMenu> simulationPopupMenus = new HashMap<Class, ULCPopupMenu>()
    Map<Class, ULCPopupMenu> batchRunPopupMenus = new HashMap<Class, ULCPopupMenu>()
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
        ULCPopupMenu popupMenu = getPopupMenu(node)
        component.setComponentPopupMenu(popupMenu)
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

    private ULCPopupMenu getPopupMenu(INavigationTreeNode node) {
        getOrCreatePopuMenu(popupMenus, node, node.class)
    }

    private ULCPopupMenu getPopupMenu(SimulationNode node) {
        getOrCreatePopuMenu(simulationPopupMenus, node, node.itemNodeUIItem.model.modelClass)
    }

    private ULCPopupMenu getPopupMenu(ItemGroupNode node) {
        getOrCreatePopuMenu(popupMenus, node, node.itemClass)
    }

    private ULCPopupMenu getPopupMenu(BatchRunNode node) {
        getOrCreatePopuMenu(batchRunPopupMenus, node, node.class)
    }

    private ULCPopupMenu getPopupMenu(ParameterizationNode node) {
        getOrCreatePopuMenu(workflowMenus, node, node.status)
    }

    private ULCPopupMenu getPopupMenu(ResourceNode node) {
        getOrCreatePopuMenu(resourceWorkflowMenus, node, node.status)
    }

    // frahman - this seems the code that precooks a single exemplar popup menu per class of target.
    // All batch nodes will get the first batchnode menu ever created because it will be stored in that map.
    // So once it gets created bad, it stays bad..
    //
    private ULCPopupMenu getOrCreatePopuMenu(Map popupMenuMap, INavigationTreeNode node, Object key) {
        if (popupMenuMap.containsKey(key)) return popupMenuMap.get(key)
        ULCPopupMenu menu = node.getPopupMenu(tree)
        addUserSpecificMenuItems(menu, node)
        //TEMPORARILY don't cache batchrun menus, see if this fixes the batch report menu - BINGO!
        //This is probably just a sticking plaster on the wound - but I don't have time or understanding
        //for a redesign..
 //       if(popupMenuMap != batchRunPopupMenus) This completely slows down the tree drawing to unacceptable levels. Must find a better way to ensure batchnodes don't get stuck with an empty report menu.
        // (Discovered this was the cause by pausing in dbg many times and catching it in the proces of throwing some stupid exception durng the infamous 'intersect' call.)
        //https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-2677 more details
        popupMenuMap.put(key, menu)
        return menu
    }

    private void addUserSpecificMenuItems(ULCPopupMenu popupMenu, INavigationTreeNode node) {
        List<ULCTableTreeMenuItemCreator> additionalMenuItems = getMenuItems(node)
        if (additionalMenuItems) {
            popupMenu.addSeparator()
            additionalMenuItems.each { ULCTableTreeMenuItemCreator c ->
                popupMenu.add(c.createComponent(tree))
            }
        }
    }

    private List<ULCTableTreeMenuItemCreator> getMenuItems(INavigationTreeNode node) {
        PopupMenuRegistry.get(node.class)
    }

    private List<ULCTableTreeMenuItemCreator> getMenuItems(ItemGroupNode node) {
        PopupMenuRegistry.get(node)
    }
}