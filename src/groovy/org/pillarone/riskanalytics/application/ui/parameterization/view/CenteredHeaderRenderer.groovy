package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultTableTreeHeaderCellRenderer
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.application.ui.util.SeriesColor
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CenteredHeaderRenderer extends DefaultTableTreeHeaderCellRenderer {
    int columnIndex = -1

    def CenteredHeaderRenderer() {
    }

    def CenteredHeaderRenderer(columnIndex) {
        this.columnIndex = columnIndex;
    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        setBackground(tableTree)
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        component.horizontalAlignment = ULCLabel.CENTER
        component.setToolTipText String.valueOf(value)

        return component
    }

    private void setBackground(ULCTableTree tableTree) {
        if (columnIndex > -1) {
            int pIndex = columnIndex % tableTree.model.getParameterizationsSize()
            Color color = UIUtils.toULCColor(SeriesColor.seriesColorList[pIndex])
            setBackground(color)
            setForeground UIUtils.getFontColor(color)
        }
    }

}
