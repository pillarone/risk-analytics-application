package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCToolBar
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.ULCTreeSelectionModel
import org.pillarone.riskanalytics.application.dataaccess.function.Mean
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingFunctionView
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.result.action.PercisionAction
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeColumn
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources

class ResultView extends AbstractModellingFunctionView {

    ULCCloseableTabbedPane tabbedPane
    P1RATModel p1ratModel

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
            it.setCellRenderer(new ResultViewTableTreeNodeCellRenderer(tabbedPane, model.treeModel.simulationRun, tree, model, this))
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        tree.rowHeaderTableTree.selectionModel.setSelectionMode(ULCTreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION)
        model.periodCount.times {int index ->
            ULCTableTreeColumn column = new ResultTableTreeColumn(index + 1, tree.viewPortTableTree, new Mean())
            column.setMinWidth(110)
            column.setHeaderRenderer(new CenteredHeaderRenderer())
            tree.viewPortTableTree.addColumn column
        }
    }

    protected void addPrecisionFunctions(ULCToolBar toolbar) {
        selectionToolbar.addSeparator()
        selectionToolbar.add new ULCButton(new PercisionAction(model, -1, "reducePrecision"))
        selectionToolbar.add new ULCButton(new PercisionAction(model, +1, "increasePrecision"))
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
