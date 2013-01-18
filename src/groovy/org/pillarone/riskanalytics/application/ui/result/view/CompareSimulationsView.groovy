package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.datatype.IDataType
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.ULCTreeSelectionModel
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.base.action.TreeCollapser
import org.pillarone.riskanalytics.application.ui.base.action.TreeExpander
import org.pillarone.riskanalytics.application.ui.base.model.EnumI18NComboBoxModel
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingFunctionView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.result.action.ApplySelectionAction
import org.pillarone.riskanalytics.application.ui.result.model.CompareSimulationsViewModel
import org.pillarone.riskanalytics.application.ui.result.model.QuantileFunctionType
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeColumn
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.util.SeriesColor
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.util.SimulationUtilities
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.dataaccess.function.*
import org.pillarone.riskanalytics.application.ui.result.action.keyfigure.*

class CompareSimulationsView extends AbstractModellingFunctionView implements ICompareFunctionListener {

    ULCCloseableTabbedPane tabbedPane
    CompareSimulationsCriteriaView criteriaView
    ULCComboBox selectView
    EnumI18NComboBoxModel profitFunctionModel

    public static int space = 3

    public CompareSimulationsView(CompareSimulationsViewModel model, RiskAnalyticsMainModel mainModel) {
        super(model, mainModel)
        model.addFunctionListener(this)

    }

    protected void initTree() {

        int treeWidth = UIUtils.calculateTreeWidth(model.treeModel.root)
        tree = new ULCFixedColumnTableTree(model.treeModel, 1, ([treeWidth] + ([100] * (model.treeModel.columnCount - 1))) as int[], true, false)
        tree.viewPortTableTree.name = "resultDescriptorTreeContent"
        tree.rowHeaderTableTree.name = "resultDescriptorTreeRowHeader"
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = UIUtils.getText(this.class, "NameColumnHeader")
        tree.setCellSelectionEnabled true
        tree.name = "CompareSimulationViewTree"
        tree.rowHeaderTableTree.columnModel.getColumns().eachWithIndex {it, i ->
            if (i == 0) {
                it.setCellRenderer(new CompareResultsTreeNodeCellRenderer(tree))
                it.setHeaderRenderer(new CenteredHeaderRenderer())
            }
        }
        tree.rowHeaderTableTree.selectionModel.setSelectionMode(ULCTreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION)
        addColumns()
    }

    public ULCBoxPane createSelectionPane() {
        selectView = new ULCComboBox(model.selectionViewModel)
        selectView.name = "selectView"
        selectView.setMinimumSize(new Dimension(120, 20))
        selectView.addActionListener(new ApplySelectionAction(model, this))

        filterSelection = new ULCComboBox()
        filterSelection.name = "filter"
        filterSelection.addItem(getText("all"))
        model.nodeNames.each {
            filterSelection.addItem it
        }

        filterLabel = new ULCLabel(UIUtils.getIcon("filter-active.png"))

        ULCBoxPane filters = new ULCBoxPane(4, 1)
        filters.add(selectView)
        filters.add(filterLabel)
        filters.add(filterSelection)
        filters.add(ULCBoxPane.BOX_EXPAND_CENTER, new ULCFiller())
        return filters
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
                ULCTableTreeColumn column = new ResultTableTreeColumn(i, tree.viewPortTableTree, null)
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
        final ULCToggleButton minButton = new ULCToggleButton()
        minButton.action = new ToggleKeyFigureAction(new MinFunction(), new DefaultToggleValueProvider(minButton), model, tree.viewPortTableTree)
        minButton.name = "minButton"
        toolbar.add(minButton)
        final ULCToggleButton maxButton = new ULCToggleButton()
        maxButton.action = new ToggleKeyFigureAction(new MaxFunction(), new DefaultToggleValueProvider(maxButton), model, tree.viewPortTableTree)
        maxButton.name = "maxButton"
        toolbar.add(maxButton)
        final ULCToggleButton sigmaButton = new ULCToggleButton()
        sigmaButton.action = new ToggleKeyFigureAction(new SigmaFunction(), new DefaultToggleValueProvider(sigmaButton), model, tree.viewPortTableTree)
        sigmaButton.name = "sigmaButton"
        toolbar.add(sigmaButton)

        toolbar.addSeparator()
        addDoubleFunctions(toolbar)

        toolbar.addSeparator()
        addPrecisionFunctions(toolbar)
    }

    private def addDoubleFunctions(ULCToolBar toolbar) {
        toolbar.add(new ULCLabel(getText("Add")))
        toolbar.add ULCFiller.createHorizontalStrut(5)
        IDataType dataType = DataTypeFactory.numberDataType
        dataType.classType = Double
        dataType.minFractionDigits = 1
        dataType.maxFractionDigits = 2
        dataType.groupingUsed = false
        ULCTextField functionValue = new ULCTextField()
        functionValue.dataType = dataType
        functionValue.value = new Double(99.5)
        functionValue.columns = 6
        if (!this.profitFunctionModel)
            this.profitFunctionModel = new EnumI18NComboBoxModel(QuantileFunctionType.values() as Object[])
        final ULCComboBox profitComboBox = new ULCComboBox(profitFunctionModel)
        toolbar.add profitComboBox
        toolbar.add ULCFiller.createHorizontalStrut(3)
        toolbar.add functionValue
        toolbar.add ULCFiller.createHorizontalStrut(3)
        toolbar.add new ULCLabel(getText("Percent"))
        toolbar.add ULCFiller.createHorizontalStrut(5)
        ULCButton percentileButton = new ULCButton(new PercentileKeyFigureAction(new QuantilePerspectiveValueProvider<Double>(functionValue, profitComboBox), model, tree.viewPortTableTree))
        percentileButton.name = "percentileButton"
        toolbar.add percentileButton
        ULCButton varButton = new ULCButton(new VarKeyFigureAction(new QuantilePerspectiveValueProvider<Double>(functionValue, profitComboBox), model, tree.viewPortTableTree))
        varButton.name = "varButton"
        toolbar.add varButton
        ULCButton tVarButton = new ULCButton(new TvarKeyFigureAction(new QuantilePerspectiveValueProvider<Double>(functionValue, profitComboBox), model, tree.viewPortTableTree))
        tVarButton.name = "tVarButton"
        toolbar.add tVarButton
    }

    private def addPrecisionFunctions(ULCToolBar toolbar) {
        toolbar.add new ULCButton(new PrecisionAction(model, -1, "reducePrecision"))
        toolbar.add new ULCButton(new PrecisionAction(model, +1, "increasePrecision"))
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
        if (validate(tableTree, value)) {
            double maxValue = tableTree.model.maxValue
            double minValue = tableTree.model.minValue
            Color bgColor = SimulationUtilities.getColor(value, minValue, maxValue)
            setBackground bgColor
        } else {
            setBackground Color.white
        }
    }

    boolean validate(ULCTableTree tableTree, def value) {
        if (value == null || Double.NaN == value) return false
        return (value instanceof Number && tableTree.model.getSimulationRunIndex(columnIndex) < 0)
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
            Color color = UIUtils.toULCColor(SeriesColor.seriesColorList[tableTree.model.getSimulationRunIndex(columnIndex)])
            setBackground(color)
            setForeground UIUtils.getFontColor(color)
        } else {
            IFunction function = tableTree.model.getFunction(columnIndex)
            if (function instanceof AbstractComparisonFunction) {
                Color color = UIUtils.toULCColor(SeriesColor.seriesColorList[tableTree.model.getSimulationRunIndex(tableTree.model.getSecondRun(columnIndex))])
                setBackground(color)
                setForeground UIUtils.getFontColor(color)
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
        nodePopup.add(new ULCMenuItem(new TreeCollapser(tree)))
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

