package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.ui.base.model.NavigationTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.core.workflow.Status

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MainSelectionTableTreeCellRenderer extends DefaultTableTreeCellRenderer {

    ULCTableTree tree
    RiskAnalyticsMainModel mainModel
    Map<String, ULCPopupMenu> popupMenus = [:]
    Map<Status, ULCPopupMenu> workflowMenus = new HashMap<Status, ULCPopupMenu>()

    public MainSelectionTableTreeCellRenderer(ULCTableTree tree, RiskAnalyticsMainModel mainModel) {
        this.tree = tree
        this.mainModel = mainModel
    }


    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        setFont(node)
        IRendererComponent component = super.getTableTreeCellRendererComponent(tree, value, selected, expanded, leaf, hasFocus, node)
        //todo fja by class
        ((ULCComponent) component).setComponentPopupMenu(((NavigationTreeNode) node).getPopupMenu(this, tree))
        ((ULCComponent) component).setToolTipText(((NavigationTreeNode) node).getToolTip())
        setIcon(((NavigationTreeNode) node).getIcon())
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

}