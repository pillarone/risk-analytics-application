package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCTableTree

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleCollectorValueTableTreeCellRenderer extends NumberFormatRenderer {

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        setFormat(value)
        setHorizontalAlignment(RIGHT)
        def renderer = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        return renderer
    }

}
