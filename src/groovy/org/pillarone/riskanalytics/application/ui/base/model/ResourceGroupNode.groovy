package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.util.ULCIcon


class ResourceGroupNode extends DefaultMutableTableTreeNode implements  INavigationTreeNode {

    ResourceGroupNode(String name) {
        super([name] as Object[])
    }

    Font getFont(String fontName, int fontSize) {
        return new Font(fontName, Font.PLAIN, fontSize)
    }

    String getName() {
        return name
    }

    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        return null
    }

    ULCIcon getIcon() {
        return null
    }

    String getToolTip() {
        return ""
    }
}
