package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.ULCTreeSelectionModel
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.dataaccess.function.MeanFunction
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingFunctionView
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.result.action.ApplySelectionAction
import org.pillarone.riskanalytics.application.ui.result.action.keyfigure.PrecisionAction
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeColumn
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.base.application.*

class ResultView extends AbstractModellingFunctionView {

    ULCCloseableTabbedPane tabbedPane
    P1RATModel p1ratModel
    //view selection for simulation/calculation
    ULCComboBox selectView
    CommentAndErrorView commentAndErrorView
    ULCSplitPane splitPane

    public static int space = 3

    public ResultView(ResultViewModel model) {
        super(model)
        tabbedPane = new ULCDetachableTabbedPane()
        tabbedPane.addTabListener([tabClosing: {TabEvent event -> event.getClosableTabbedPane().closeCloseableTab(event.getTabClosingIndex())}] as ITabListener)
    }

    protected void initTree() {

        int treeWidth = UIUtils.calculateTreeWidth(model.treeModel.root)

        tree = new ULCFixedColumnTableTree(model.treeModel, 1, ([treeWidth] + ([100] * (model.treeModel.columnCount - 1))) as int[], true, false)
        tree.viewPortTableTree.name = "resultDescriptorTreeContent"
        tree.rowHeaderTableTree.name = "resultDescriptorTreeRowHeader"
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = getText("NameColumnHeader")
        tree.setCellSelectionEnabled true

        tree.rowHeaderTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(new ResultViewTableTreeNodeCellRenderer(this, -1))
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        tree.rowHeaderTableTree.selectionModel.setSelectionMode(ULCTreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION)
        model.periodCount.times {int index ->
            ULCTableTreeColumn column = new ResultTableTreeColumn(index + 1, this, new MeanFunction())
            column.setMinWidth(110)
            column.setHeaderRenderer(new CenteredHeaderRenderer())
            tree.viewPortTableTree.addColumn column
        }
        tree.rowHeaderView.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK))
        tree.rowHeaderView.registerKeyboardAction(ctrlaction, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK), ULCComponent.WHEN_FOCUSED)
        commentAndErrorView.tableTree = tree
    }

    protected void initComponents() {
        commentAndErrorView = new CommentAndErrorView(model)
        super.initComponents()
    }

    protected void addPrecisionFunctions(ULCToolBar toolbar) {
        selectionToolbar.addSeparator()
        selectionToolbar.add new ULCButton(new PrecisionAction(model, -1, "reducePrecision"))
        selectionToolbar.add new ULCButton(new PrecisionAction(model, +1, "increasePrecision"))
    }

    public ULCBoxPane createSelectionPane() {
        selectView = new ULCComboBox(model.selectionViewModel)
        selectView.name = "selectView"
        selectView.setPreferredSize(new Dimension(120, 20))
        selectView.addActionListener(new ApplySelectionAction(model, this))

        filterSelection = new ULCComboBox()
        filterSelection.name = "filter"
        filterSelection.addItem(getText("all"))
        model.nodeNames.each {
            filterSelection.addItem it
        }

        filterLabel = new ULCLabel(UIUtils.getIcon("filter-active.png"))

        ULCBoxPane filters = new ULCBoxPane(3, 1)
        filters.add(ULCBoxPane.BOX_EXPAND_CENTER, selectView)
        filters.add(filterLabel)
        filters.add(filterSelection)
        return filters
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
