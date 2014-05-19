package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.main.view.CreateReportsMenu
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

@CompileStatic
class ItemNode extends DefaultMutableTableTreeNode implements INavigationTreeNode {

    private final ModellingUIItem itemNodeUIItem
    private final Map values = [:]

    ItemNode(ModellingUIItem itemNodeUIItem, boolean leaf) {
        super([itemNodeUIItem.nameAndVersion] as Object[], leaf)
        this.itemNodeUIItem = itemNodeUIItem;
    }

    Map getValues() {
        return values
    }

    ModellingUIItem getItemNodeUIItem() {
        return itemNodeUIItem
    }

    VersionNumber getVersionNumber() {
        return itemNodeUIItem.versionNumber
    }

    Class getItemClass() {
        return itemNodeUIItem.itemClass
    }

    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        return null
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
        return itemNodeUIItem.name
    }
    // Definitely called on first click to open Batches subtree in the left pane of GUI..
    // and for some bad reason, the reporting menu is never regenerated on a per-batch-node basis afterwards.
    // (ah, that reason is, the menus are cached in MainSelectionTableTreeCellRenderer)
    void addReportMenus(ULCPopupMenu simulationNodePopUpMenu, ULCTableTree tree, boolean separatorNeeded, boolean reloadOnBecomingVisible = false) {

        if (!(this instanceof IReportableNode)) {
            throw new RiskAnalyticsInconsistencyException(this.toString() + """ asked for report menu; but NOT reportable item. Please report to development. """)
        }
        CreateReportsMenu reportsMenu = new CreateReportsMenu("Reports", this as IReportableNode, tree, simulationNodePopUpMenu, reloadOnBecomingVisible)
        if (separatorNeeded) {
            simulationNodePopUpMenu.addSeparator()
        }
        simulationNodePopUpMenu.add(reportsMenu)
    }

    String getUserObject() {
        itemNodeUIItem.nameAndVersion
    }

    void setUserObject(String userObject) {
        itemNodeUIItem.item.rename(userObject)
        setValueAt(itemNodeUIItem.nameAndVersion, 0)
    }

    String toString() {
        userObject
    }
}
