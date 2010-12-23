package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleCollectorView {
    ULCBoxPane content
    ULCFixedColumnTableTree tree
    AbstractTableTreeModel tableTreeModel

    public SingleCollectorView(AbstractTableTreeModel tableTreeModel) {
        this.tableTreeModel = tableTreeModel
    }

    public void init() {
        tableTreeModel.init()
        initTree()
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void initTree() {

        def columnsWidths = 120

        tree = new ULCFixedColumnTableTree(tableTreeModel, 1, ([200] + [columnsWidths] * (tableTreeModel.columnCount - 1)) as int[])
        tree.name = "SingleCollectorTableTree"
        tree.viewPortTableTree.setRootVisible(false);
        tree.viewPortTableTree.showsRootHandles = true

        tree.rowHeaderTableTree.name = "SingleCollectorTreeRowHeader"
        tree.rowHeaderTableTree.setRootVisible(false);
        tree.rowHeaderTableTree.showsRootHandles = true
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = "Name"
        tree.cellSelectionEnabled = true
        SingleCollectorTableTreeNodeCellRenderer collectorTableTreeNodeCellRenderer = new SingleCollectorTableTreeNodeCellRenderer()
        tree.rowHeaderTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(collectorTableTreeNodeCellRenderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        SingleCollectorValueTableTreeCellRenderer renderer = new SingleCollectorValueTableTreeCellRenderer()
        tree.viewPortTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(renderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

    }

    protected void initComponents() {
        content = new ULCBoxPane()
        content.name = "SingleCollectorTreeViewContent"
    }

    private void layoutComponents() {
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, tree)
    }

    private void attachListeners() {

    }


}
