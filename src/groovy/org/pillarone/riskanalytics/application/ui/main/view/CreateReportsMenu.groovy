package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenu
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.base.action.CreateReportAction
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.ReportFactory
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.event.IPopupMenuListener
import com.ulcjava.base.application.event.PopupMenuEvent

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
    List<IReportModel> reportModels
    ULCPopupMenu parent

    CreateReportsMenu(String s, List<IReportModel> reportModel, ULCTableTree tree, ULCPopupMenu simulationNodePopUpMenu) {
        super(s)
        this.ulcTableTree = tree
        this.reportModels = reportModel
        this.parent = simulationNodePopUpMenu
        simulationNodePopUpMenu.addPopupMenuListener(this)
    }

    void popupMenuHasBecomeVisible(PopupMenuEvent popupMenuEvent) {
        addIndividualReportMenus()
    }

    void popupMenuHasBecomeInvisible(PopupMenuEvent popupMenuEvent) {
        parent.removePopupMenuListener(this)
    }

    void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
        parent.removePopupMenuListener(this)
    }

    /**
     * This this method we add all the potnetial reports, but don't show them. We can do this quickly. Deciding whether
     * or not to show the reports expensive context information. Don't retrieve it until the user has explicitly asked for it.
     */
    // I'm glad I don't drink - fr
    private addIndividualReportMenus() {
        for (IReportModel aModel in reportModels) {
            for (ReportFactory.ReportFormat aReportFormat in ReportFactory.ReportFormat) {
                CreateReportAction action = new CreateReportAction(aModel, aReportFormat, ulcTableTree)
                CreateReportMenuItem createReportMenuItem = new CreateReportMenuItem(action, this)
                createReportMenuItem.checkVisibility()
                add(createReportMenuItem)
            }
        }
    }
}
