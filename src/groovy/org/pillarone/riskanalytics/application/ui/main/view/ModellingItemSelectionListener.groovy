package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.event.TableTreeModelEvent
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.server.ULCTableTreeModelAdapter
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode

class ModellingItemSelectionListener extends ULCTableTreeModelAdapter {
    double scrollPosition
    boolean syncSelection
    Integer selectedRow
    TreePath selectedPath
    private ULCFixedColumnTableTree tree

    ModellingItemSelectionListener(ULCFixedColumnTableTree tree) {
        super(tree.rowHeaderTableTree.model)
        this.tree = tree
    }

    void rememberSelectionState() {
        syncSelection = true
        scrollPosition = tree.verticalScrollBar.position
        selectedRow = tree.rowHeaderTableTree.selectedRows?.toList()?.min()
        selectedPath = tree.rowHeaderTableTree.selectedPath
    }

    void flushSelectionState() {
        syncSelection = false
    }

    private boolean isSelectionInvalid(TreePath path) {
        path && path.pathCount != DefaultTableTreeModel.getPathToRoot(path.lastPathComponent)?.length
    }

    @Override
    void tableTreeNodesInserted(TableTreeModelEvent event) {
        if (!syncSelection) {
            return
        }
        List<TreePath> treePaths = []
        event.children.each { ITableTreeNode node ->
            if (isSelectionInvalid(selectedPath)) {
                treePaths << new TreePath(DefaultTableTreeModel.getPathToRoot(node))
            }
        }
        if (treePaths) {
            tree.rowHeaderTableTree.setPathSelection(treePaths.toArray() as TreePath[])
        }
        tree.verticalScrollBar.position = scrollPosition
    }

    @Override
    void tableTreeNodeStructureChanged(TableTreeModelEvent event) {
        if (!syncSelection) {
            return
        }
        if (isSelectionInvalid(selectedPath)) {
            if (event.treePath.lastPathComponent.childCount == 0) {
                tree.rowHeaderTableTree.pathSelection = event.treePath
            } else if (tree.rowHeaderTableTree.getPathForRow(selectedRow)?.lastPathComponent instanceof ItemNode) {
                tree.rowHeaderTableTree.rowSelection = selectedRow
            } else {
                tree.rowHeaderTableTree.pathSelection = event.treePath
            }
        } else {
            if (selectedPath) {
                tree.rowHeaderTableTree.pathSelection = selectedPath
            } else {
                tree.rowHeaderTableTree.clearSelection()
            }
        }
        tree.verticalScrollBar.position = scrollPosition
    }

    @Override
    void tableTreeNodesRemoved(TableTreeModelEvent event) {
        super.tableTreeNodesRemoved(event)
    }
}
