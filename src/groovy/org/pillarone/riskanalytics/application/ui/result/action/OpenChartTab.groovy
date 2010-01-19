package org.pillarone.riskanalytics.application.ui.result.action

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.HTMLUtilities
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.chart.view.ChartView
import org.pillarone.riskanalytics.application.ui.chart.view.ChartViewFactory
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.view.ChartRenameListener
import org.pillarone.riskanalytics.application.ui.result.view.ChartType

class OpenChartTab extends ResourceBasedAction {
    ULCCloseableTabbedPane tabbedPane
    ChartType chartType
    String title
    def rowHeaderTableTree
    SimulationRun simulationRun


    public OpenChartTab(ULCCloseableTabbedPane tabbedPane, String key, ChartType chartType, SimulationRun simulationRun, def rowHeaderTableTree) {
        super(key)
        this.@tabbedPane = tabbedPane
        this.@chartType = chartType
        this.@title = getValue(IAction.NAME)
        this.simulationRun = simulationRun
        this.@rowHeaderTableTree = rowHeaderTableTree
        putValue(IAction.SMALL_ICON, ChartViewFactory.getChartIcon(chartType));
    }

    public void doActionPerformed(ActionEvent event) {
        def paths = rowHeaderTableTree.selectedPaths.lastPathComponent
        List nodes = paths.findAll {it instanceof ResultTableTreeNode}
        ChartView view = ChartViewFactory.getChart(chartType, title, simulationRun, nodes)
        ULCBoxPane tabContent = view.content
        if (tabContent) {
            tabbedPane.addTab(title, ChartViewFactory.getChartIcon(chartType), tabContent)
            String toolTipText = ""
            StringBuffer text = new StringBuffer()
            nodes.each {
                text << it.getDisplayPath() + "<br>"
            }

            if (text.length() > 0) {
                tabbedPane.setToolTipTextAt(tabbedPane.tabCount - 1, HTMLUtilities.convertToHtml(text.toString()))
            }
            tabbedPane.selectedIndex = tabbedPane.tabCount - 1
            view.model.addListener(new ChartRenameListener(tabbedPane, tabbedPane.selectedIndex, view.model))
        }
    }
}
