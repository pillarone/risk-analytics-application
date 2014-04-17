package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import groovy.transform.CompileStatic

@CompileStatic
class ResourceGroupNode extends DefaultMutableTableTreeNode implements INavigationTreeNode {

    ResourceGroupNode(String name) {
        super([name] as Object[])
    }

    @Override
    Font getFont(String fontName, int fontSize) {
        return new Font(fontName, Font.PLAIN, fontSize)
    }

    @Override
    String getName() {
        return name
    }

    @Override
    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        return null
    }

    @Override
    ULCIcon getIcon() {
        return null
    }

    @Override
    String getToolTip() {
        return ""
    }

    @Override
    Class getItemClass() {
        return null
    }
}
