package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenu
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.IPopupMenuListener
import com.ulcjava.base.application.event.PopupMenuEvent
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.base.action.CreateReportAction
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.ReportFactory
import org.pillarone.riskanalytics.core.report.ReportRegistry

/**
 * sparten
 *
 * This class aims to dely the creation of context report menus until such time as they are actually required. It should
 * speed inital creation of popup menus, requiring data analysis only when a user has expressed interest in actually
 * reporting on a given item.
 *
 * That comment above is highly ironic given the bug I have been battling over the last couple days. -fr
 */
class CreateReportsMenu extends ULCMenu implements IPopupMenuListener {

    ULCTableTree ulcTableTree
    ULCPopupMenu parent
    private final IReportableNode reportableNode
    private final boolean reloadOnBecomingVisible

    CreateReportsMenu(String name, IReportableNode reportableNode, ULCTableTree tree, ULCPopupMenu simulationNodePopUpMenu, boolean reloadOnBecomingVisible) {
        super(name)
        this.reloadOnBecomingVisible = reloadOnBecomingVisible
        this.reportableNode = reportableNode
        this.ulcTableTree = tree
        this.parent = simulationNodePopUpMenu
        simulationNodePopUpMenu.addPopupMenuListener(this)
        visible = !reportModels.empty
    }

    void popupMenuHasBecomeVisible(PopupMenuEvent popupMenuEvent) {
        addIndividualReportMenus()
    }

    void popupMenuHasBecomeInvisible(PopupMenuEvent popupMenuEvent) {
        if (reloadOnBecomingVisible) {
            removeAll()
        } else {
            parent.removePopupMenuListener(this)
        }

    }

    void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
        if (reloadOnBecomingVisible) {
            removeAll()
        } else {
            parent.removePopupMenuListener(this)
        }
    }

    /**
     * This this method we add all the potnetial reports, but don't show them. We can do this quickly. Deciding whether
     * or not to show the reports expensive context information. Don't retrieve it until the user has explicitly asked for it.
     */
    private addIndividualReportMenus() {
        ArrayList<IReportModel> reports = getReportModels()
        if (reports.empty) {
            visible = false
        } else {
            visible = true
            for (IReportModel aModel in reports) {
                for (ReportFactory.ReportFormat aReportFormat in ReportFactory.ReportFormat.values()) {
                    CreateReportAction action = new CreateReportAction(aModel, aReportFormat, ulcTableTree)
                    CreateReportMenuItem createReportMenuItem = new CreateReportMenuItem(action, this)
                    createReportMenuItem.checkVisibility()
                    add(createReportMenuItem)
                }
            }
        }
    }

    private ArrayList<IReportModel> getReportModels() {
        List<Class> modelsToDisplay = reportableNode.modelsToReportOn()
        List<IReportModel> reports = new ArrayList<IReportModel>(ReportRegistry.getReportModel(modelsToDisplay))
        reports
    }
}
