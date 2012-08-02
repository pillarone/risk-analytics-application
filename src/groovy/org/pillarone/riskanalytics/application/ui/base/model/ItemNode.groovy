package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon

import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.ReportFactory
import org.pillarone.riskanalytics.core.report.ReportRegistry
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

import com.ulcjava.base.application.ULCMenu
import org.pillarone.riskanalytics.application.ui.base.action.CreateReportAction
import org.pillarone.riskanalytics.application.ui.main.view.CreateReportMenuItem

import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException
import org.pillarone.riskanalytics.application.ui.main.view.CreateReportsMenu

class ItemNode extends DefaultMutableTableTreeNode implements INavigationTreeNode {

    AbstractUIItem abstractUIItem
    boolean renameable
    Map values = [:]

    public ItemNode(AbstractUIItem abstractUIItem, boolean leaf = true, boolean renameable = true) {
        super([abstractUIItem?.item?.name] as Object[])
        this.abstractUIItem = abstractUIItem;
        this.renameable = renameable
    }

    public ItemNode(AbstractUIItem abstractUIItem, name, boolean leaf, boolean renameable) {
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
        return abstractUIItem.item.name
    }

    public void addReportMenus(ULCPopupMenu simulationNodePopUpMenu, ULCTableTree tree, boolean separatorNeeded) {

        if (!(this instanceof IReportableNode)) {
            throw new RiskAnalyticsInconsistencyException(this.toString() + """ has been asked to present reports for a
                    popup menu, but is not a reportable item. Please report to development. """)
        }

        if (((IReportableNode) this).showReports()) {
            List<Class> modelsToDisplay = ((IReportableNode) this).modelsToReportOn()
            List<IReportModel> reports = ReportRegistry.getReportModel( modelsToDisplay )
            if (!reports.empty) {
                ULCMenu reportsMenu = new ULCMenu("Reports")
                for (IReportModel model in reports) {
                    for (ReportFactory.ReportFormat aReportFormat in ReportFactory.ReportFormat) {
                        reportsMenu.add(new CreateReportMenuItem(new CreateReportAction(model, aReportFormat, tree, abstractUIItem.mainModel)))
                    }
                }
                if (separatorNeeded) simulationNodePopUpMenu.addSeparator();
                simulationNodePopUpMenu.add(reportsMenu)
            }
        }
    }
}
