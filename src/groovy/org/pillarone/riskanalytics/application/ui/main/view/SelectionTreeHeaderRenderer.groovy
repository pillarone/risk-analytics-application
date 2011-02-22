package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultTableTreeHeaderCellRenderer

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeHeaderRenderer extends DefaultTableTreeHeaderCellRenderer {
    int columnIndex = -1
    ULCPopupMenu menu

    def SelectionTreeHeaderRenderer(columnIndex) {
        this.columnIndex = columnIndex;
    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        component.horizontalAlignment = ULCLabel.CENTER
        component.setToolTipText String.valueOf(value)

        return component
    }


}
