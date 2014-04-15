package org.pillarone.riskanalytics.application.ui.base.model
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.main.view.CreateReportsMenu
import org.pillarone.riskanalytics.application.ui.main.view.item.ItemNodeUIItem
import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.ReportRegistry
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class ItemNode extends DefaultMutableTableTreeNode implements INavigationTreeNode {

    ItemNodeUIItem itemNodeUIItem
    boolean renameable
    Map values = [:]

    public ItemNode(ItemNodeUIItem itemNodeUIItem, boolean leaf = true, boolean renameable = true) {
        super([itemNodeUIItem?.name] as Object[])
        this.itemNodeUIItem = itemNodeUIItem;
        this.renameable = renameable
    }

    public ItemNode(ItemNodeUIItem itemNodeUIItem, name, boolean leaf, boolean renameable) {
        super([name] as Object[])
        this.itemNodeUIItem = itemNodeUIItem;
        this.renameable = renameable
    }

    VersionNumber getVersionNumber() {
        return itemNodeUIItem.versionNumber
    }

    Class getItemClass() {
        return itemNodeUIItem.itemClass
    }

    public ULCPopupMenu getPopupMenu(ULCTableTree tree) {
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

    public String getName() {
        return itemNodeUIItem.name
    }
    // Definitely called on first click to open Batches subtree in the left pane of GUI..
    // and for some bad reason, the reporting menu is never regenerated on a per-batch-node basis afterwards.
    // (ah, that reason is, the menus are cached in MainSelectionTableTreeCellRenderer)
    public void addReportMenus(ULCPopupMenu simulationNodePopUpMenu, ULCTableTree tree, boolean separatorNeeded) {

        if (!(this instanceof IReportableNode)) {
            throw new RiskAnalyticsInconsistencyException(this.toString() + """ asked for report menu; but NOT reportable item. Please report to development. """)
        }

        List<Class> modelsToDisplay = ((IReportableNode) this).modelsToReportOn() // Returns empty list as 'Bjorns First Batch' happened to be empty.
        List<IReportModel> reports = new ArrayList<IReportModel>()
        reports.addAll(ReportRegistry.getReportModel(modelsToDisplay)) //TODO rename method to getReportModels and test.
        if (!reports.empty) { //Fails here consequently
            CreateReportsMenu reportsMenu = new CreateReportsMenu("Reports", reports, tree, itemNodeUIItem.mainModel, simulationNodePopUpMenu)
            reportsMenu.visible = true
            if (separatorNeeded) simulationNodePopUpMenu.addSeparator();
            simulationNodePopUpMenu.add(reportsMenu)
        }
    }
}
