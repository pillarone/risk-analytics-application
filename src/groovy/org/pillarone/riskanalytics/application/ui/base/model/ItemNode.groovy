package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.ReportFactory
import org.pillarone.riskanalytics.core.report.ReportRegistry
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCMenu
import org.pillarone.riskanalytics.application.ui.base.action.CreateReportAction
import org.pillarone.riskanalytics.application.ui.main.view.CreateReportMenuItem
import org.pillarone.riskanalytics.application.ui.main.action.OpenItemAction
import org.pillarone.riskanalytics.application.ui.main.action.SimulationAction
import org.pillarone.riskanalytics.application.ui.main.view.CompareParameterizationMenuItem
import org.pillarone.riskanalytics.application.ui.main.action.CompareParameterizationsAction
import org.pillarone.riskanalytics.application.ui.main.action.TagsAction
import org.pillarone.riskanalytics.application.ui.main.action.RenameAction
import org.pillarone.riskanalytics.application.ui.main.action.SaveAsAction
import org.pillarone.riskanalytics.application.ui.main.action.CreateNewMajorVersion
import org.pillarone.riskanalytics.application.ui.main.action.ExportItemAction
import org.pillarone.riskanalytics.application.UserContext
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.ui.main.action.ChooseDealAction
import org.pillarone.riskanalytics.application.ui.main.action.workflow.StartWorkflowAction
import org.pillarone.riskanalytics.application.ui.main.action.DeleteAction

class ItemNode extends DefaultMutableTableTreeNode implements INavigationTreeNode {

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

    protected void addReportMenus(ULCPopupMenu simulationNodePopUpMenu, ULCTableTree tree, boolean separatorNeeded) {
        List<IReportModel> reports = ReportRegistry.getReportModel(abstractUIItem.model.modelClass)
        if (!reports.empty) {
            ULCMenu reportsMenu = new ULCMenu("Reports")
            for (IReportModel model in reports) {
                reportsMenu.add(new CreateReportMenuItem(new CreateReportAction(model, ReportFactory.ReportFormat.PDF, tree, abstractUIItem.mainModel)))
                reportsMenu.add(new CreateReportMenuItem(new CreateReportAction(model, ReportFactory.ReportFormat.PPT, tree, abstractUIItem.mainModel)))
//                reportsMenu.add(new CreateReportMenuItem(new CreateReportAction(model, ReportFactory.ReportFormat.XLS, tree, abstractUIItem.mainModel)))

                // Support for export to Excel prepared, but not activated since need coordination with IC
                // reportsMenu.add(new ULCMenuItem(new CreateXlsReportAction(model, tree, abstractUIItem.mainModel)))
            }
            if (separatorNeeded) simulationNodePopUpMenu.addSeparator();
            simulationNodePopUpMenu.add(reportsMenu)
        }
    }
}
