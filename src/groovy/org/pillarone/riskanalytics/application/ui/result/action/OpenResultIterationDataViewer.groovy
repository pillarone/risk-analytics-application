package org.pillarone.riskanalytics.application.ui.result.action

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.HTMLUtilities
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.view.ChartType
import org.pillarone.riskanalytics.application.ui.result.view.ResultIterationDataView

class OpenResultIterationDataViewer extends ResourceBasedAction {
    String title
    ULCCloseableTabbedPane tabbedPane
    ChartType chartType
    def rowHeaderTableTree
    SimulationRun simulationRun
    def resultView


    public OpenResultIterationDataViewer(ULCCloseableTabbedPane tabbedPane, SimulationRun simulationRun, def rowHeaderTableTree, def resultView) {
        super("IterationData")
        this.@tabbedPane = tabbedPane
        this.simulationRun = simulationRun
        this.@rowHeaderTableTree = rowHeaderTableTree
        this.@title = getValue(IAction.NAME)
        this.@resultView = resultView
    }


    public void doActionPerformed(ActionEvent event) {
        def paths = rowHeaderTableTree.selectedPaths.lastPathComponent
        List nodes = paths.findAll {it instanceof ResultTableTreeNode}
        ResultIterationDataViewModel rawDataViewModel = new ResultIterationDataViewModel(simulationRun, nodes, true, resultView)
        ULCBoxPane tabContent = new ResultIterationDataView(rawDataViewModel).content
        if (tabContent) {
            tabbedPane.addTab(title, getValue(IAction.SMALL_ICON), tabContent)
            String toolTipText = ""
            StringBuffer text = new StringBuffer()
            nodes.each {
                text << it.getDisplayPath() + "<br>"
            }

            if (text.length() > 0) {
                tabbedPane.setToolTipTextAt(tabbedPane.tabCount - 1, HTMLUtilities.convertToHtml(text.toString()))
            }
            tabbedPane.selectedIndex = tabbedPane.tabCount - 1
        }
    }

}