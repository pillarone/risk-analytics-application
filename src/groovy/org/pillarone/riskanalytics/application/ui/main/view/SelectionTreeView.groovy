package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.tree.ULCTreeSelectionModel
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.batch.action.TreeDoubleClickAction
import org.pillarone.riskanalytics.application.ui.main.action.DeleteAction
import org.pillarone.riskanalytics.application.ui.main.action.RenameAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeView {

    AbstractTableTreeModel tableTreeModel
    ULCFixedColumnTableTree tree
    ULCBoxPane content
    P1RATModel p1RATModel

    public SelectionTreeView(P1RATModel p1RATModel) {
        this.p1RATModel = p1RATModel
        initTree()
        initComponents()
        layoutComponents()
        attachListeners()
    }


    protected void initComponents() {
        content = new ULCBoxPane()
        content.name = "treeViewContent"
    }

    private void layoutComponents() {
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, tree)
    }

    private void attachListeners() {
        tree.rowHeaderTableTree.addActionListener(new TreeDoubleClickAction(tree.rowHeaderTableTree, p1RATModel))
        tree.rowHeaderTableTree.registerKeyboardAction(new DeleteAction(tree.rowHeaderTableTree, p1RATModel), KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), ULCComponent.WHEN_FOCUSED)
        tree.rowHeaderTableTree.registerKeyboardAction(new RenameAction(tree.rowHeaderTableTree, p1RATModel), KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true), ULCComponent.WHEN_FOCUSED)
    }

    protected void initTree() {

        int treeWidth = 200
        def columnsWidths = 120

        tree = new ULCFixedColumnTableTree(p1RATModel.selectionTreeModel, 1, ([treeWidth] + [columnsWidths] * 8) as int[])
        tree.name = "selectionTableTree"

        tree.viewPortTableTree.columnModel.getColumns().eachWithIndex {ULCTableTreeColumn it, int index ->
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        MainSelectionTableTreeCellRenderer renderer = getPopUpRenderer(tree)

        tree.rowHeaderTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(renderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        tree.rowHeaderTableTree.name = "selectionTreeRowHeader"
        tree.rowHeaderTableTree.setRootVisible(true);
        tree.rowHeaderTableTree.showsRootHandles = true
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = "Name"
        tree.cellSelectionEnabled = true
        tree.rowHeaderTableTree.getSelectionModel().setSelectionMode(ULCTreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        tree.getRowHeaderTableTree().expandPaths([new TreePath([p1RATModel.selectionTreeModel.root] as Object[])] as TreePath[], false);
    }

    public MainSelectionTableTreeCellRenderer getPopUpRenderer(ULCFixedColumnTableTree tree) {
        MainSelectionTableTreeCellRenderer renderer = new MainSelectionTableTreeCellRenderer(tree.rowHeaderTableTree, p1RATModel)
        renderer.initPopUpMenu()
        return renderer
    }


}
