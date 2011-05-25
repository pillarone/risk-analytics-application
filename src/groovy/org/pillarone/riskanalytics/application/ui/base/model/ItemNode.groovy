package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class ItemNode extends DefaultMutableTableTreeNode implements NavigationTreeNode {

    AbstractUIItem abstractUIItem
    boolean renameable
    Map values = [:]

    public ItemNode(AbstractUIItem abstractUIItem, leaf = true, renameable = true) {
        super([abstractUIItem?.item?.name] as Object[])
        this.abstractUIItem = abstractUIItem;
        this.renameable = renameable
    }

    public ItemNode(AbstractUIItem abstractUIItem, name, leaf, renameable) {
        super([name] as Object[])
        this.abstractUIItem = abstractUIItem;
        this.renameable = renameable
    }

    VersionNumber getVersionNumber() {
        return abstractUIItem.item.versionNumber
    }

    Class getItemClass() {
        return abstractUIItem.item.class
    }

    public ULCPopupMenu getPopupMenu(MainSelectionTableTreeCellRenderer renderer, ULCTableTree tree) {
        return null
    }

    public ULCIcon getIcon() {
        return null
    }

    public Font getFont(String fontName, int fontSize) {
        return new Font(fontName, Font.PLAIN, fontSize)
    }

    public String getToolTip() {
        return ""
    }


}
