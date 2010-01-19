package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.datatype.IDataType
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.ULCTreeSelectionModel
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.util.SimulationUtilities
import org.pillarone.riskanalytics.application.dataaccess.function.CompareFunction
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.base.action.TreeExpander
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingFunctionView
import org.pillarone.riskanalytics.application.ui.main.view.P1RATMainView
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.result.model.CompareSimulationsViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeColumn
import org.pillarone.riskanalytics.application.ui.result.view.CompareSimulationsCriteriaView
import org.pillarone.riskanalytics.application.ui.result.view.ICompareFunctionListener
import org.pillarone.riskanalytics.application.ui.result.view.NumberFormatRenderer
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.result.action.*

class CompareSimulationsView extends AbstractModellingFunctionView implements ICompareFunctionListener {

    ULCCloseableTabbedPane tabbedPane
    CompareSimulationsCriteriaView criteriaView
    P1RATMainView p1RATMainView


    public static int space = 3

    public CompareSimulationsView(CompareSimulationsViewModel model, P1RATMainView p1RATMainView) {
        super(model)
        model.addFunctionListener(this)
        this.p1RATMainView = p1RATMainView
    }

    protected void initTree() {

        int treeWidth = UIUtils.calculateTreeWidth(model.treeModel.root)
        tree = new ULCFixedColumnTableTree(model.treeModel, 1, ([treeWidth] + ([100] * (model.treeModel.columnCount - 1))) as int[], true, false)
        tree.viewPortTableTree.name = "resultDescriptorTreeContent"
        tree.rowHeaderTableTree.name = "resultDescriptorTreeRowHeader"
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = UIUtils.getText(this.class, "NameColumnHeader")
        tree.setCellSelectionEnabled true

        tree.rowHeaderTableTree.columnModel.getColumns().eachWithIndex {it, i ->
            if (i == 0) {
                it.setCellRenderer(new CompareResultsTreeNodeCellRenderer(tree))
                it.setHeaderRenderer(new CenteredHeaderRenderer())
            }
        }
        tree.rowHeaderTableTree.selectionModel.setSelectionMode(ULCTreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION)
        addColumns()
    }

    public void functionAdded(IFunction function) {
        refreshNodes()
    }

    public void functionRemoved(IFunction function) {
        refreshNodes()
    }

    public void functionsChanged() {
        refreshNodes()
    }

    protected void addColumns() {
        for (int i = 1; i < model.treeModel.columnCount; i++) {
            if (!model.treeModel.isHidden(i)) {
                ULCTableTreeColumn column = new ResultTableTreeColumn(i, tree.viewPortTableTree)
                column.setMinWidth(110)
                column.setHeaderRenderer(new CompareHeaderRenderer(i))
                column.setCellRenderer(new CompareRenderer(i))
                tree.viewPortTableTree.addColumn column
            }
        }
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ResultView." + key);
    }

    protected ULCContainer layoutContent(ULCContainer content) {
        ULCBoxPane contentPane = new ULCBoxPane(1, 2)
        ULCBoxPane functionPane = new ULCBoxPane(0, 1)

        contentPane.add(ULCBoxPane.BOX_LEFT_TOP, functionPane)
        ULCSplitPane pane = new ULCSplitPane(ULCSplitPane.VERTICAL_SPLIT)
        pane.oneTouchExpandable = true
        pane.setResizeWeight(1)
        pane.setDividerSize(10)
        pane.add(content); pane.add(criteriaView.content)
        pane.setDividerLocation(0.65)
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, pane)

        tabbedPane.addTab(getText("TreeView"), UIUtils.getIcon(getText("TreeView.icon")), contentPane)
        tabbedPane.setCloseableTab(0, false)
        return tabbedPane
    }

    protected void initComponents() {
        super.initComponents();
        tabbedPane = new ULCDetachableTabbedPane()
        tabbedPane.addTabListener([tabClosing: {TabEvent event -> event.getClosableTabbedPane().closeCloseableTab(event.getTabClosingIndex())}] as ITabListener)

        criteriaView = new CompareSimulationsCriteriaView(this, model, tree)
    }



    protected void addToolBarElements(ULCToolBar toolbar) {
        toolbar.add(new ULCToggleButton(new MinAction(model, tree.viewPortTableTree)))
        toolbar.add(new ULCToggleButton(new MaxAction(model, tree.viewPortTableTree)))
        toolbar.add(new ULCToggleButton(new SigmaAction(model, tree.viewPortTableTree)))

        toolbar.addSeparator()
        addDoubleFunctions(toolbar)

        toolbar.addSeparator()
        addPrecisionFunctions(toolbar)
    }

    private def addDoubleFunctions(ULCToolBar toolbar) {
        toolbar.add(new ULCLabel(getText("Add")))
        toolbar.add ULCFiller.createHorizontalStrut(5)
        IDataType dataType = new ULCNumberDataType(ClientContext.locale)
        dataType.integer = false
        dataType.minFractionDigits = 1
        dataType.maxFractionDigits = 2
        dataType.groupingUsed = false
        ULCTextField functionValue = new ULCTextField()
        functionValue.dataType = dataType
        functionValue.value = 99.5
        functionValue.columns = 6
        toolbar.add functionValue
        toolbar.add ULCFiller.createHorizontalStrut(3)
        toolbar.add new ULCLabel(getText("Percent"))
        toolbar.add ULCFiller.createHorizontalStrut(5)
        toolbar.add new ULCButton(new PercentileAction(model, tree.viewPortTableTree, functionValue))
        toolbar.add new ULCButton(new VarAction(model, tree.viewPortTableTree, functionValue))
        toolbar.add new ULCButton(new TvarAction(model, tree.viewPortTableTree, functionValue))
    }

    private def addPrecisionFunctions(ULCToolBar toolbar) {
        toolbar.add new ULCButton(new PercisionAction(model, -1, "reducePrecision"))
        toolbar.add new ULCButton(new PercisionAction(model, +1, "increasePrecision"))
    }


}

class CompareDeterministicsView extends CompareSimulationsView {

    public CompareDeterministicsView(CompareSimulationsViewModel model) {
        super(model)
    }


    protected void addToolBarElements(ULCToolBar toolbar) {
        toolbar.addSeparator()
        addPrecisionFunctions(toolbar)
    }

}


class CompareRenderer extends NumberFormatRenderer {

    int columnIndex

    public CompareRenderer() {
    }

    public CompareRenderer(columnIndex) {
        this.columnIndex = columnIndex;
    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        updateFractionDigits(tableTree)
        setFormat(value)
        setBackground(tableTree, value)
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        component.horizontalAlignment = ULCLabel.RIGHT
        component.setToolTipText String.valueOf(value)
        return component
    }




    private def setBackground(ULCTableTree tableTree, value) {

        if (value != null && value instanceof Number && tableTree.model.getSimulationRunIndex(columnIndex) < 0) {
            double maxValue = tableTree.model.maxValue
            double minValue = tableTree.model.minValue
            Color bgColor = SimulationUtilities.getColor(value, minValue, maxValue)
            setBackground bgColor
        } else {
            setBackground Color.white
        }
    }
}

class CompareHeaderRenderer extends CenteredHeaderRenderer {

    int columnIndex


    public CompareHeaderRenderer(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node);
        int simualtionIndex = tableTree.model.getSimulationRunIndex(columnIndex)
        if (simualtionIndex != -1) {
            setBackground(SimulationUtilities.RESULT_VIEW_COLOR[tableTree.model.getSimulationRunIndex(columnIndex)])
        } else {
            IFunction function = tableTree.model.getFunction(columnIndex)
            if (function instanceof CompareFunction) {
                setBackground(SimulationUtilities.RESULT_VIEW_COLOR[tableTree.model.getSimulationRunIndex(function.runB)])
            }
        }
        return component
    }


}

class CompareResultsTreeNodeCellRenderer extends DefaultTableTreeCellRenderer {
    ULCPopupMenu nodePopup
    def tree


    public CompareResultsTreeNodeCellRenderer(tree) {
        this.tree = tree;
        nodePopup = new ULCPopupMenu()
        nodePopup.add(new ULCMenuItem(new TreeExpander(tree)))
    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        IRendererComponent rendererComponent = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node);
        setPopupMenu(rendererComponent, node)
        return rendererComponent;
    }


    void setPopupMenu(IRendererComponent rendererComponent, def node) {
        rendererComponent.setComponentPopupMenu(nodePopup)
    }
}

