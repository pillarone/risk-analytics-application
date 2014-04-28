package org.pillarone.riskanalytics.application.ui.result.action

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.HTMLUtilities
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.view.ChartType
import org.pillarone.riskanalytics.application.ui.result.view.ResultIterationDataView
import org.pillarone.riskanalytics.application.ui.result.view.ResultView
import org.pillarone.riskanalytics.core.output.SimulationRun

class OpenResultIterationDataViewer extends ResourceBasedAction {
    String title
    ULCCloseableTabbedPane tabbedPane
    ChartType chartType
    def rowHeaderTableTree
    SimulationRun simulationRun
    ResultView resultView


    public OpenResultIterationDataViewer(ULCCloseableTabbedPane tabbedPane, SimulationRun simulationRun,
                                         def rowHeaderTableTree, def resultView) {
        super("IterationData")
        this.@tabbedPane = tabbedPane
        this.simulationRun = simulationRun
        this.@rowHeaderTableTree = rowHeaderTableTree
        this.@title = getValue(NAME)
        this.@resultView = resultView
    }


    public void doActionPerformed(ActionEvent event) {
        def paths = rowHeaderTableTree.selectedPaths.lastPathComponent
        List<ResultTableTreeNode> nodes = paths.findAll { it instanceof ResultTableTreeNode }
        ResultIterationDataViewModel rawDataViewModel = new ResultIterationDataViewModel(simulationRun, nodes, true, true, true, resultView)
        ULCBoxPane tabContent = new ResultIterationDataView(rawDataViewModel).content
        if (tabContent) {
            tabbedPane.addTab(title, getValue(SMALL_ICON) as ULCIcon, tabContent)
            StringBuffer text = new StringBuffer()
            nodes.each {
                text << it.displayPath + "<br>"
            }

            if (text.length() > 0) {
                tabbedPane.setToolTipTextAt(tabbedPane.tabCount - 1, HTMLUtilities.convertToHtml(text.toString()))
            }
            tabbedPane.selectedIndex = tabbedPane.tabCount - 1
        }
    }
}