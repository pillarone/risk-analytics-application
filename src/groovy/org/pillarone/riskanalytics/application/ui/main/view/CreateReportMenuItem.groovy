package org.pillarone.riskanalytics.application.ui.main.view
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.event.IPopupMenuListener
import com.ulcjava.base.application.event.PopupMenuEvent
import org.pillarone.riskanalytics.application.ui.base.action.CreateReportAction
import org.pillarone.riskanalytics.core.report.IReportData
import org.pillarone.riskanalytics.core.report.IReportModel
/**
 * bzetterstrom
 */
class CreateReportMenuItem extends ULCMenuItem implements IPopupMenuListener {

    ULCPopupMenu parent

    CreateReportMenuItem(CreateReportAction action, CreateReportsMenu menu) {
        super(action)
        parent = menu.getComponentPopupMenu()
        parent.addPopupMenuListener(this)
    }

    /**
     * When a node of this type is selected, get the reports which are valid for this node.
     * @param treeSelectionEvent
     */
//    void valueChanged(TreeSelectionEvent treeSelectionEvent) {
//    }

    void popupMenuHasBecomeVisible(PopupMenuEvent popupMenuEvent) {
    }

    void popupMenuHasBecomeInvisible(PopupMenuEvent popupMenuEvent) {
    }

    void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
    }

    void checkVisibility() {
        CreateReportAction action = (CreateReportAction) getAction()
        IReportData reportData = action.getReportData()
        IReportModel model = action.reportModel
        boolean visible = model.isValidFormatAndData(action.reportFormat, reportData)
        setVisible(visible)
    }
}
