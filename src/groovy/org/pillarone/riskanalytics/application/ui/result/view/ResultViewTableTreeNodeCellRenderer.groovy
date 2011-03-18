package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.event.IPopupMenuListener
import com.ulcjava.base.application.event.PopupMenuEvent
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.base.action.OpenComponentHelp
import org.pillarone.riskanalytics.application.ui.base.action.TreeCollapser
import org.pillarone.riskanalytics.application.ui.base.action.TreeExpander
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodeCopier
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.chart.model.ChartViewModel
import org.pillarone.riskanalytics.application.ui.result.action.OpenChartTab
import org.pillarone.riskanalytics.application.ui.result.action.OpenPlotChartTab
import org.pillarone.riskanalytics.application.ui.result.action.OpenResultIterationDataViewer
import org.pillarone.riskanalytics.application.ui.result.action.ShowSingleValueCollectorAction
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy
import com.ulcjava.base.application.*

enum ChartType {
    HISTOGRAM, DiscretePDF, Scatter, DISTRIBUTIONS, PARALLEL_COORDINATES, WATERFALL, STACKED_BAR_CHART, LINE_CHART
}

class ResultViewTableTreeNodeCellRenderer extends DefaultTableTreeCellRenderer {
    ULCCloseableTabbedPane tabbedPane
    SimulationRun simulationRun
    def tree
    ULCPopupMenu nodePopup
    ULCPopupMenu nodeHelpPopup
    ULCPopupMenu defaultResultNodePopup
    ULCPopupMenu defaultSingleResultNodePopup
    ULCPopupMenu resultNodePopup
    ULCPopupMenu singleResultNodePopup
    ULCNumberDataType numberDataType

    public ResultViewTableTreeNodeCellRenderer(ULCCloseableTabbedPane tabbedPane, SimulationRun simulationRun, def tree, model, def resultView) {
        this.tabbedPane = tabbedPane
        this.simulationRun = simulationRun
        this.tree = tree

        numberDataType = DataTypeFactory.numberDataType
        numberDataType.setGroupingUsed true
        numberDataType.setMinFractionDigits 2
        numberDataType.setMaxFractionDigits 2

        resultNodePopup = new ULCPopupMenu()
        singleResultNodePopup = new ULCPopupMenu()
        defaultResultNodePopup = new ULCPopupMenu()
        defaultSingleResultNodePopup = new ULCPopupMenu()
        nodePopup = new ULCPopupMenu()
        nodeHelpPopup = new ULCPopupMenu()
        OpenComponentHelp help = new OpenComponentHelp(this.tree.rowHeaderTableTree)

        defaultResultNodePopup.add(createChartsMenu(defaultResultNodePopup, tree))
        defaultResultNodePopup.add(new ULCMenuItem(new OpenResultIterationDataViewer(tabbedPane, simulationRun, tree, resultView)))
        defaultResultNodePopup.addSeparator()
        defaultResultNodePopup.add(new ULCMenuItem(new TreeNodeCopier(rowHeaderTree: tree.getRowHeaderTableTree(), viewPortTree: tree.getViewPortTableTree(), model: model.treeModel)))
        defaultResultNodePopup.add(new ULCMenuItem(getTreeNodeCopier(tree, model)))

        defaultSingleResultNodePopup.add(createChartsMenu(defaultSingleResultNodePopup, tree))
        defaultSingleResultNodePopup.add(new ULCMenuItem(new OpenResultIterationDataViewer(tabbedPane, simulationRun, tree, resultView)))
        defaultSingleResultNodePopup.add(new ULCMenuItem(new ShowSingleValueCollectorAction(tabbedPane, tree, simulationRun)))
        defaultSingleResultNodePopup.addSeparator()
        defaultSingleResultNodePopup.add(new ULCMenuItem(new TreeNodeCopier(rowHeaderTree: tree.getRowHeaderTableTree(), viewPortTree: tree.getViewPortTableTree(), model: model.treeModel)))
        defaultSingleResultNodePopup.add(new ULCMenuItem(getTreeNodeCopier(tree, model)))

        resultNodePopup.add(new ULCMenuItem(new TreeExpander(tree)))
        resultNodePopup.add(new ULCMenuItem(new TreeCollapser(tree)))
        resultNodePopup.addSeparator()
        resultNodePopup.add(createChartsMenu(resultNodePopup, tree))
        resultNodePopup.add(new ULCMenuItem(new OpenResultIterationDataViewer(tabbedPane, simulationRun, tree, resultView)))
        resultNodePopup.addSeparator()
        resultNodePopup.add(new ULCMenuItem(new TreeNodeCopier(rowHeaderTree: tree.getRowHeaderTableTree(), viewPortTree: tree.getViewPortTableTree(), model: model.treeModel)))
        resultNodePopup.add(new ULCMenuItem(getTreeNodeCopier(tree, model)))

        singleResultNodePopup.add(new ULCMenuItem(new TreeExpander(tree)))
        singleResultNodePopup.add(new ULCMenuItem(new TreeCollapser(tree)))
        singleResultNodePopup.addSeparator()
        singleResultNodePopup.add(createChartsMenu(singleResultNodePopup, tree))
        singleResultNodePopup.add(new ULCMenuItem(new OpenResultIterationDataViewer(tabbedPane, simulationRun, tree, resultView)))
        singleResultNodePopup.add(new ULCMenuItem(new ShowSingleValueCollectorAction(tabbedPane, tree, simulationRun)))
        singleResultNodePopup.addSeparator()
        singleResultNodePopup.add(new ULCMenuItem(new TreeNodeCopier(rowHeaderTree: tree.getRowHeaderTableTree(), viewPortTree: tree.getViewPortTableTree(), model: model.treeModel)))
        singleResultNodePopup.add(new ULCMenuItem(getTreeNodeCopier(tree, model)))

        nodePopup.add(new ULCMenuItem(new TreeExpander(tree)))
        nodePopup.add(new ULCMenuItem(new TreeCollapser(tree)))
        nodePopup.addSeparator()
        nodePopup.add(new ULCMenuItem(new TreeNodeCopier(rowHeaderTree: tree.getRowHeaderTableTree(), viewPortTree: tree.getViewPortTableTree(), model: model.treeModel)))
        nodePopup.add(new ULCMenuItem(getTreeNodeCopier(tree, model)))

        nodeHelpPopup.add(new ULCMenuItem(new TreeExpander(tree)))
        nodeHelpPopup.add(new ULCMenuItem(new TreeCollapser(tree)))
        nodeHelpPopup.addSeparator()
        nodeHelpPopup.add(new ULCMenuItem(new TreeNodeCopier(rowHeaderTree: tree.getRowHeaderTableTree(), viewPortTree: tree.getViewPortTableTree(), model: model.treeModel)))
        nodeHelpPopup.add(new ULCMenuItem(getTreeNodeCopier(tree, model)))
        nodeHelpPopup.addSeparator()
        nodeHelpPopup.add(new ULCMenuItem(help))
    }

    private TreeNodeCopier getTreeNodeCopier(tree, model) {
        TreeNodeCopier copierWithPath = new TreeNodeCopier(true)
        copierWithPath.rowHeaderTree = tree.getRowHeaderTableTree()
        copierWithPath.viewPortTree = tree.getViewPortTableTree()
        copierWithPath.model = model.treeModel
        return copierWithPath
    }

    private setFormat(def value) {
        setDataType null
    }

    private setFormat(Number value) {
        setDataType(numberDataType)
    }


    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        IRendererComponent rendererComponent = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node);
        setPopupMenu(rendererComponent, node)
        setFormat(value)
        return rendererComponent;
    }

    void setPopupMenu(IRendererComponent rendererComponent, ResultTableTreeNode node) {
        if (node.childCount > 0)
            rendererComponent.setComponentPopupMenu(node.collector == SingleValueCollectingModeStrategy.IDENTIFIER ? singleResultNodePopup : resultNodePopup)
        else
            rendererComponent.setComponentPopupMenu(node.collector == SingleValueCollectingModeStrategy.IDENTIFIER ? defaultSingleResultNodePopup : defaultResultNodePopup)
    }

    void setPopupMenu(IRendererComponent rendererComponent, def node) {
        rendererComponent.setComponentPopupMenu((node instanceof ComponentTableTreeNode) ? nodeHelpPopup : nodePopup)
    }

    private ULCMenu createChartsMenu(ULCPopupMenu popupMenu, def tree) {
        ULCMenu chartsMenu = new ULCMenu("Charts")
        chartsMenu.add(new ULCMenuItem(new OpenChartTab(tabbedPane, "StackedBarChart", ChartType.STACKED_BAR_CHART, simulationRun, tree)))
        ULCMenuItem lineChartItem = new ULCMenuItem(new OpenChartTab(tabbedPane, "LineChart", ChartType.LINE_CHART, simulationRun, tree))
        lineChartItem.enabled = simulationRun.periodCount > 1
        chartsMenu.add(lineChartItem)
        chartsMenu.addSeparator()
        chartsMenu.add(new ULCMenuItem(new OpenChartTab(tabbedPane, "HistogramChart", ChartType.HISTOGRAM, simulationRun, tree)))
        chartsMenu.add(new ULCMenuItem(new OpenChartTab(tabbedPane, "Distributions", ChartType.DISTRIBUTIONS, simulationRun, tree)))
        chartsMenu.add(new ULCMenuItem(new OpenChartTab(tabbedPane, "WaterfallChart", ChartType.WATERFALL, simulationRun, tree)))
        chartsMenu.addSeparator()
        ULCMenuItem scatterPlotMenuItem = new ULCMenuItem(new OpenPlotChartTab(tabbedPane, "ScatterPlotChart", ChartType.Scatter, simulationRun, tree))
        popupMenu.addPopupMenuListener(new EnableScatterPlot(scatterPlotMenuItem, tree))
        ULCMenuItem parallelCoordinatesMenuItem = new ULCMenuItem(new OpenChartTab(tabbedPane, "ParallelCoordinatesChart", ChartType.PARALLEL_COORDINATES, simulationRun, tree))
        popupMenu.addPopupMenuListener(new EnableParallelCoordinatesChart(parallelCoordinatesMenuItem, tree))
        chartsMenu.add(scatterPlotMenuItem)
        chartsMenu.add(parallelCoordinatesMenuItem)
        return chartsMenu
    }
}

class EnableScatterPlot implements IPopupMenuListener {
    ULCMenuItem menuItem
    def rowHeaderTableTree

    public EnableScatterPlot(ULCMenuItem menuItem, def rowHeaderTableTree) {
        this.menuItem = menuItem
        this.@rowHeaderTableTree = rowHeaderTableTree
    }

    public void popupMenuHasBecomeVisible(PopupMenuEvent event) {
        def paths = rowHeaderTableTree.selectedPaths.lastPathComponent
        List nodes = paths.findAll {it instanceof ResultTableTreeNode}
        if (nodes.size() == 2) {
            menuItem.enabled = true
        } else {
            menuItem.enabled = false
        }
    }

    public void popupMenuHasBecomeInvisible(PopupMenuEvent event) {}

    public void popupMenuCanceled(PopupMenuEvent event) {}
}

class EnableParallelCoordinatesChart implements IPopupMenuListener {
    ULCMenuItem menuItem
    def rowHeaderTableTree

    public EnableParallelCoordinatesChart(ULCMenuItem menuItem, def rowHeaderTableTree) {
        this.menuItem = menuItem
        this.@rowHeaderTableTree = rowHeaderTableTree
    }

    public void popupMenuHasBecomeVisible(PopupMenuEvent event) {
        def paths = rowHeaderTableTree.selectedPaths.lastPathComponent
        List nodes = paths.findAll {it instanceof ResultTableTreeNode}
        if (nodes.size() > 1) {
            menuItem.enabled = true
        } else {
            menuItem.enabled = false
        }
    }

    public void popupMenuHasBecomeInvisible(PopupMenuEvent event) {}

    public void popupMenuCanceled(PopupMenuEvent event) {}
}




class ChartRenameListener implements IModelChangedListener {
    ULCCloseableTabbedPane tabbedPane
    int panelIndex
    ChartViewModel model

    public ChartRenameListener(ULCCloseableTabbedPane tabbedPane, int panelIndex, ChartViewModel model) {
        this.@tabbedPane = tabbedPane
        this.@panelIndex = panelIndex
        this.@model = model
    }

    public void modelChanged() {
        try {
            tabbedPane.setTitleAt(panelIndex, format(model.chartProperties.title))
        } catch (Exception ex) {
            //ignore exception due an undocked pane
        }
    }

    private String format(String title) {
        return (title.length() > 12) ? (title.substring(0, 12) + "...") : title
    }
}

