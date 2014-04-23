package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.INavigationTreeNode
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

@CompileStatic
class BatchRootNode extends DefaultMutableTableTreeNode implements INavigationTreeNode {
    private final RiskAnalyticsMainModel mainModel

    BatchRootNode(String name, RiskAnalyticsMainModel mainModel) {
        super([name] as Object[], false)
        this.mainModel = mainModel
    }

    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        return new ULCPopupMenu()
    }

    ULCIcon getIcon() {
        return null
    }

    Font getFont(String fontName, int fontSize) {
        return new Font(fontName, Font.PLAIN, fontSize)
    }

    String getToolTip() {
        return ""
    }

    String getName() {
        return ""
    }

    @Override
    Class getItemClass() {
        return null
    }
}
