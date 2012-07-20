package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenu
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.base.action.CreateReportAction
import com.ulcjava.base.application.event.ISelectionChangedListener
import com.ulcjava.base.application.event.SelectionChangedEvent
import com.ulcjava.base.application.event.IFocusListener
import com.ulcjava.base.application.event.FocusEvent
import org.pillarone.riskanalytics.core.report.IReportData
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.ReportFactory
import java.awt.event.FocusListener
import java.awt.event.FocusEvent
import com.ulcjava.base.application.event.FocusEvent
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.event.IPopupMenuListener
import com.ulcjava.base.application.event.PopupMenuEvent
import java.awt.event.MouseAdapter

/**
 * sparten
 *
 * This class aims to dely the creation of context report menus until such time as they are actually required. It should
 * speed inital creation of popup menus, requiring data analysis only when a user has expressed interest in actually
 * reporting on a given item.
 *
 */
class CreateReportsMenu extends ULCMenu implements IPopupMenuListener {

    RiskAnalyticsMainModel raMainModel
     ULCTableTree ulcTableTree
     List<IReportModel> reportModels
    ULCPopupMenu parent

    CreateReportsMenu(String s, List<IReportModel> reportModel,ULCTableTree tree, RiskAnalyticsMainModel model, ULCPopupMenu simulationNodePopUpMenu) {
        super(s)
        this.raMainModel = model
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
    private addIndividualReportMenus(){
        for (IReportModel aModel in reportModels ) {
            for (ReportFactory.ReportFormat aReportFormat in ReportFactory.ReportFormat) {
                CreateReportAction action = new CreateReportAction(aModel, aReportFormat, ulcTableTree, raMainModel)
                CreateReportMenuItem createReportMenuItem = new CreateReportMenuItem(action , this)
                createReportMenuItem.visible = false
                add(createReportMenuItem)
            }
        }
    }
}
