package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.main.action.CreateDefaultResourceAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

class ResourceClassNode extends DefaultMutableTableTreeNode implements INavigationTreeNode {

    String name
    Class resourceClass
    RiskAnalyticsMainModel mainModel

    ResourceClassNode(String name, Class resourceClass, RiskAnalyticsMainModel mainModel) {
        super([name] as Object[])
        this.name = name
        this.resourceClass = resourceClass
        this.mainModel = mainModel
    }

    Font getFont(String fontName, int fontSize) {
        return new Font(fontName, Font.PLAIN, fontSize)
    }

    String getName() {
        return name
    }

    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        ULCPopupMenu menu = new ULCPopupMenu()
        menu.add(new ULCMenuItem(new CreateDefaultResourceAction(tree, mainModel)))

        return menu
    }

    ULCIcon getIcon() {
        return null
    }

    String getToolTip() {
        return ""
    }
}
