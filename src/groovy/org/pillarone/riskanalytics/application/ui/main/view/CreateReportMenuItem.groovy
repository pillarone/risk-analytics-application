package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.event.ITreeSelectionListener
import org.pillarone.riskanalytics.application.ui.base.action.CreateReportAction
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemUtils
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.IReportData
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.report.impl.ReportDataCollection
import org.pillarone.riskanalytics.core.report.impl.ModellingItemReportData

/**
 * bzetterstrom
 */
class CreateReportMenuItem extends ULCMenuItem implements ITreeSelectionListener {

    CreateReportMenuItem(CreateReportAction action) {
        super(action)
        ULCTableTree tree = action.getTree()
        tree.addTreeSelectionListener(this)
    }

    void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        CreateReportAction action = (CreateReportAction) getAction()
        List<AbstractUIItem> selectedUIItems = action.getSelectedUIItems()
        Collection<ModellingItem> selectedModellingItems = UIItemUtils.getSelectedModellingItems(selectedUIItems)
        Collection<IReportData> reportData = selectedModellingItems.collectAll { ModellingItem modellingItem -> new ModellingItemReportData(modellingItem) }
        IReportModel model = action.getReportModel()
        setVisible(model.isValidFormatAndData(action.reportFormat, new ReportDataCollection(reportData)))
    }



}
