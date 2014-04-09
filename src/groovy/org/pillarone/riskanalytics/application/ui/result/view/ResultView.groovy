package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingFunctionView
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.comment.view.NavigationListener
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.result.action.ApplySelectionAction
import org.pillarone.riskanalytics.application.ui.result.action.keyfigure.PrecisionAction
import org.pillarone.riskanalytics.application.ui.result.model.AbstractResultViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources

import static com.ulcjava.base.application.ULCComponent.WHEN_FOCUSED
import static com.ulcjava.base.application.tree.ULCTreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
import static com.ulcjava.base.shared.IDefaults.BOX_EXPAND_CENTER

class ResultView extends AbstractModellingFunctionView implements NavigationListener {

    ULCCloseableTabbedPane tabbedPane
    //view selection for simulation/calculation
    ULCComboBox selectView
    CommentAndErrorView commentAndErrorView
    ULCSplitPane splitPane

    public ResultView(AbstractResultViewModel model, RiskAnalyticsMainModel mainModel) {
        super(model, mainModel)
    }

    @Override
    AbstractResultViewModel getModel() {
        super.model as AbstractResultViewModel
    }

    @Override
    protected void preViewCreationInitialization() {
        super.preViewCreationInitialization()
        tabbedPane = new ULCDetachableTabbedPane()
        tabbedPane.addTabListener([tabClosing: { TabEvent event -> event.closableTabbedPane.closeCloseableTab(event.tabClosingIndex) }] as ITabListener)
    }

    protected void initTree() {

        int treeWidth = UIUtils.calculateTreeWidth(model.treeModel.root)
        tree = new ULCFixedColumnTableTree(model.treeModel, 1, ([treeWidth] + ([100] * (model.treeModel.columnCount - 1))) as int[], true, false)
        tree.viewPortTableTree.name = "resultDescriptorTreeContent"
        tree.rowHeaderTableTree.name = "resultDescriptorTreeRowHeader"
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = getText("NameColumnHeader")
        tree.cellSelectionEnabled = true

        tree.rowHeaderTableTree.columnModel.columns.each { ULCTableTreeColumn it ->
            it.cellRenderer = new ResultViewTableTreeNodeCellRenderer(this, -1)
            it.headerRenderer = new CenteredHeaderRenderer()
        }

        tree.rowHeaderTableTree.selectionModel.selectionMode = DISCONTIGUOUS_TREE_SELECTION
        tree.rowHeaderView.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK))
        tree.rowHeaderView.registerKeyboardAction(ctrlaction, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK), WHEN_FOCUSED)
        commentAndErrorView.tableTree = tree
    }

    protected void initComponents() {
        commentAndErrorView = new CommentAndErrorView(model)
        model.addNavigationListener this
        super.initComponents()
    }

    protected void addPrecisionFunctions() {
        selectionToolbar.addSeparator()
        selectionToolbar.add new ULCButton(new PrecisionAction(model, -1, "reducePrecision"))
        selectionToolbar.add new ULCButton(new PrecisionAction(model, +1, "increasePrecision"))
    }

    public ULCBoxPane createSelectionPane() {
        selectView = new ULCComboBox(model.selectionViewModel)
        selectView.name = "selectView"
        selectView.preferredSize = new Dimension(300, 20)
        selectView.addActionListener(new ApplySelectionAction(model, this))

        filterSelection = new ULCComboBox()
        filterSelection.name = "filter"
        filterSelection.addItem(getText("all"))
        model.nodeNames.each {
            filterSelection.addItem it
        }
        filterLabel = new ULCLabel(UIUtils.getIcon("filter-active.png"))

        ULCBoxPane filters = new ULCBoxPane(3, 1)
        filters.add(BOX_EXPAND_CENTER, selectView)
        filters.add(filterLabel)
        filters.add(filterSelection)
        return filters
    }

    public void showHiddenComments() {
        if ((NO_DIVIDER - splitPane.dividerLocationRelative) < 0.1)
            splitPane.setDividerLocation(DIVIDER)
        else
            splitPane.setDividerLocation(NO_DIVIDER)
    }

    public void showComments() {
        if ((NO_DIVIDER - splitPane.dividerLocationRelative) < 0.1) {
            splitPane.setDividerLocation(DIVIDER)
        }
    }

    void selectTab(int index) {
        tabbedPane.selectedIndex = index
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
}
