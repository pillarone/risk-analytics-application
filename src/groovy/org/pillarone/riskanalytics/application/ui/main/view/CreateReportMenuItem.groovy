package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem

import com.ulcjava.base.application.event.ITreeSelectionListener
import org.pillarone.riskanalytics.application.ui.base.action.CreateReportAction
import com.ulcjava.base.application.event.TreeSelectionEvent

import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemUtils
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.IReportData
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.report.impl.ReportDataCollection
import org.pillarone.riskanalytics.core.report.impl.ModellingItemReportData
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import com.ulcjava.base.application.ULCMenu
import com.ulcjava.base.application.event.IFocusListener
import com.ulcjava.base.application.event.FocusEvent
import com.ulcjava.base.application.event.IPopupMenuListener
import com.ulcjava.base.application.event.PopupMenuEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import java.awt.event.FocusListener
import java.awt.event.FocusEvent
import com.ulcjava.base.application.event.FocusEvent
import java.awt.event.FocusEvent
import com.ulcjava.base.application.event.FocusEvent
import java.awt.event.WindowListener
import com.ulcjava.base.application.ULCPopupMenu

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
        IReportModel model = action.getReportModel()
        boolean visible = model.isValidFormatAndData(action.reportFormat, reportData)
        setVisible(visible)
    }
}
