package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.canoo.ulc.community.renderer.server.ULCHeaderRenderComponent
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.ITableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class IconHeaderCellRenderer extends ULCHeaderRenderComponent implements ITableTreeCellRenderer {

    IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        setIcon UIUtils.getImageIcon("P1RATLogoSmall.png")
        return this;
    }
}

