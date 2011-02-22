package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class FilteredCenteredHeaderRenderer extends CenteredHeaderRenderer {

    boolean isFiltered

    def IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        if (isFiltered) {
            setIcon(UIUtils.getIcon("add-mouseover.png"))
        } else {
            setIcon(UIUtils.getIcon("chartline1-active.png"))
        }
        return component
    }

}
