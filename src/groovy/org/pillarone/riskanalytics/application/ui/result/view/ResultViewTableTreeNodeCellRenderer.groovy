package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.HTMLUtilities
import org.pillarone.riskanalytics.application.ui.base.action.OpenComponentHelp
import org.pillarone.riskanalytics.application.ui.base.action.TreeCollapser
import org.pillarone.riskanalytics.application.ui.base.action.TreeExpander
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodeCopier
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.view.ShowCommentsMenuItem
import org.pillarone.riskanalytics.application.ui.comment.action.InsertCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.ShowCommentsAction
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.result.action.OpenChartTab
import org.pillarone.riskanalytics.application.ui.result.action.OpenPlotChartTab
import org.pillarone.riskanalytics.application.ui.result.action.OpenResultIterationDataViewer
import org.pillarone.riskanalytics.application.ui.result.action.ShowSingleValueCollectorAction
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy
import com.ulcjava.base.application.*

class ResultViewTableTreeNodeCellRenderer extends DefaultTableTreeCellRenderer {
    ULCCloseableTabbedPane tabbedPane
    SimulationRun simulationRun
    def tree
    ULCPopupMenu nodePopup = new ULCPopupMenu()
    ULCPopupMenu nodeHelpPopup = new ULCPopupMenu()
    ULCPopupMenu defaultResultNodePopup = new ULCPopupMenu()
    ULCPopupMenu defaultSingleResultNodePopup = new ULCPopupMenu()
    ULCPopupMenu resultNodePopup = new ULCPopupMenu()
    ULCPopupMenu singleResultNodePopup = new ULCPopupMenu()
    ULCNumberDataType numberDataType
    CommentAndErrorView commentAndErrorView
    def model
    InsertCommentAction insertComment
    ShowCommentsAction showCommentsAction
    OpenComponentHelp help
    OpenResultIterationDataViewer openResultIterationDataViewer
    ShowSingleValueCollectorAction showSingleValueCollectorAction
    TreeNodeCopier treeNodeCopierWithPath
    TreeNodeCopier treeNodeCopierWithoutPath
    TreeExpander treeExpander
    TreeCollapser treeCollapser
    int columnIndex = -1


    public ResultViewTableTreeNodeCellRenderer(ResultView resultView, int columnIndex) {
        this.tabbedPane = resultView.tabbedPane
        this.tree = resultView.tree
        this.commentAndErrorView = resultView.commentAndErrorView
        this.model = resultView.model
        this.simulationRun = resultView.model.treeModel.simulationRun
        this.columnIndex = columnIndex
        initContextMenu(resultView)

    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        IRendererComponent rendererComponent = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node);
        setPopupMenu(rendererComponent, node)
        customizeNode(rendererComponent, node)
        setFormat(value)
        return rendererComponent;
    }


    public void initContextMenu(ResultView resultView) {
        initActions(resultView)

        ULCMenu defaultResultChartMenu = createChartsMenu(defaultResultNodePopup, tree, tabbedPane, simulationRun)
        addActionsMenu(defaultResultNodePopup, [defaultResultChartMenu, openResultIterationDataViewer, null, treeNodeCopierWithoutPath, treeNodeCopierWithPath, null, insertComment, createShowCommentsMenuItem()])

        ULCMenu defaultSingleResultChartMenu = createChartsMenu(defaultSingleResultNodePopup, tree, tabbedPane, simulationRun)
        addActionsMenu(defaultSingleResultNodePopup, [defaultSingleResultChartMenu, openResultIterationDataViewer, showSingleValueCollectorAction, null, treeNodeCopierWithoutPath, treeNodeCopierWithPath, null, insertComment, createShowCommentsMenuItem()])

        ULCMenu resultChartMenu = createChartsMenu(resultNodePopup, tree, tabbedPane, simulationRun)
        addActionsMenu(resultNodePopup, [treeExpander, treeCollapser, null, resultChartMenu, openResultIterationDataViewer, null, treeNodeCopierWithoutPath, treeNodeCopierWithPath, null, insertComment, createShowCommentsMenuItem()])

        ULCMenu singleResultChartMenu = createChartsMenu(singleResultNodePopup, tree, tabbedPane, simulationRun)
        addActionsMenu(singleResultNodePopup, [treeExpander, treeCollapser, null, singleResultChartMenu, openResultIterationDataViewer, showSingleValueCollectorAction, null, treeNodeCopierWithoutPath, treeNodeCopierWithPath, null, insertComment, createShowCommentsMenuItem()])

        addActionsMenu(nodePopup, [treeExpander, treeCollapser, null, treeNodeCopierWithoutPath, treeNodeCopierWithPath, null, insertComment, createShowCommentsMenuItem()])

        addActionsMenu(nodeHelpPopup, [treeExpander, treeCollapser, null, treeNodeCopierWithoutPath, treeNodeCopierWithPath, null, insertComment, createShowCommentsMenuItem(), null, help])
    }



    private void initActions(ResultView resultView) {
        insertComment = new InsertCommentAction(tree.rowHeaderTableTree, columnIndex)
        insertComment.addCommentListener commentAndErrorView
        showCommentsAction = new ShowCommentsAction(tree.rowHeaderTableTree, columnIndex, false)
        showCommentsAction.addCommentListener commentAndErrorView

        openResultIterationDataViewer = new OpenResultIterationDataViewer(tabbedPane, simulationRun, tree, resultView)
        showSingleValueCollectorAction = new ShowSingleValueCollectorAction(tabbedPane, tree, simulationRun)

        help = new OpenComponentHelp(this.tree.rowHeaderTableTree)
        treeNodeCopierWithPath = getTreeNodeCopier(tree, model, true)
        treeNodeCopierWithoutPath = getTreeNodeCopier(tree, model, false)

        treeExpander = new TreeExpander(tree)
        treeCollapser = new TreeCollapser(tree)
    }

    void customizeNode(IRendererComponent rendererComponent, def node) {
        Font font = getFont()
        if (node.comments && node.comments.size() > 0) {
            setForeground(Color.black)
            setFont(font.deriveFont(Font.BOLD))
            setToolTipText(HTMLUtilities.convertToHtml(node.commentMessage))
        } else {
            setForeground(Color.black)
            setFont(font.deriveFont(Font.PLAIN))
            setToolTipText node.getToolTip()
        }

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

    private setFormat(def value) {
        setDataType null
    }

    private setFormat(Number value) {
        setDataType(getNumberDataType())
    }

    ULCNumberDataType getNumberDataType() {
        if (!numberDataType) {
            numberDataType = DataTypeFactory.numberDataType
            numberDataType.setGroupingUsed true
            numberDataType.setMinFractionDigits 2
            numberDataType.setMaxFractionDigits 2
        }
        return numberDataType
    }

    private TreeNodeCopier getTreeNodeCopier(tree, model, boolean copyWithPath) {
        TreeNodeCopier copierWithPath = new TreeNodeCopier(copyWithPath)
        copierWithPath.rowHeaderTree = tree.getRowHeaderTableTree()
        copierWithPath.viewPortTree = tree.getViewPortTableTree()
        copierWithPath.model = model.treeModel
        return copierWithPath
    }

    void addActionsMenu(ULCPopupMenu popupMenu, List menuItems) {
        for (def menuItem: menuItems) {
            menuItem ? addMenu(popupMenu, menuItem) : popupMenu.addSeparator()
        }
    }

    private void addMenu(ULCPopupMenu popupMenu, AbstractAction action) {
        popupMenu.add(new ULCMenuItem(action))
    }

    private void addMenu(ULCPopupMenu popupMenu, ULCMenu submenu) {
        popupMenu.add(submenu)
    }

    private void addMenu(ULCPopupMenu popupMenu, ULCMenuItem menuItem) {
        popupMenu.add(menuItem)
    }

    private void addMenu(ULCPopupMenu popupMenu, def submenu) {
        throw new Exception("${submenu} is not supported")
    }


    public ULCMenu createChartsMenu(ULCPopupMenu popupMenu, def tree, def tabbedPane, SimulationRun simulationRun) {
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

    ULCMenuItem createShowCommentsMenuItem() {
        ULCMenuItem showCommentsMenuItem = new ShowCommentsMenuItem(showCommentsAction, model)
        tree.addTreeSelectionListener(showCommentsMenuItem)
        return showCommentsMenuItem
    }

}








