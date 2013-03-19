package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleCollectorTableTreeNodeCellRenderer extends DefaultTableTreeCellRenderer {

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        return component

    }

}
