package org.pillarone.riskanalytics.application.ui.resulttemplate.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.base.action.OpenComponentHelp
import org.pillarone.riskanalytics.application.ui.base.action.TreeCollapser
import org.pillarone.riskanalytics.application.ui.base.action.TreeExpander
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode

class ResultConfigurationTableTreeNodeRenderer extends DefaultTableTreeCellRenderer {
    private def tree
    private ULCPopupMenu menu
    private ULCPopupMenu helpMenu


    public ResultConfigurationTableTreeNodeRenderer(tree) {
        this.tree = tree;
        menu = new ULCPopupMenu()
        menu.add(new ULCMenuItem(new TreeExpander(tree)))
        menu.add(new ULCMenuItem(new TreeCollapser(tree)))
        OpenComponentHelp help = new OpenComponentHelp(this.tree.rowHeaderTableTree)
        helpMenu = new ULCPopupMenu()
        helpMenu.add(new ULCMenuItem(new TreeExpander(tree)))
        helpMenu.add(new ULCMenuItem(new TreeCollapser(tree)))
        helpMenu.addSeparator()
        helpMenu.add(new ULCMenuItem(help))

    }



    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        addExpandNodePopupMenuEntry(component, node, tableTree)
        setToolTipText(node.getToolTip())
        return component

    }

    void addExpandNodePopupMenuEntry(IRendererComponent rendererComponent, def node, def tableTree) {
        if (!node.isLeaf() && (node instanceof ComponentTableTreeNode)) {
            rendererComponent.setComponentPopupMenu(helpMenu)
        } else if (!node.isLeaf()) {
            rendererComponent.setComponentPopupMenu(menu)
        } else {
            rendererComponent.setComponentPopupMenu(null)

        }

    }

}