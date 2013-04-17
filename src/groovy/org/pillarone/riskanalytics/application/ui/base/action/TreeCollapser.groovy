package org.pillarone.riskanalytics.application.ui.base.action

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.application.IAction
import groovy.transform.CompileStatic

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class TreeCollapser extends ResourceBasedAction {

    ULCFixedColumnTableTree tree

    public TreeCollapser(ULCFixedColumnTableTree tree) {
        super("CollapseNode")
        this.tree = tree
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true));
    }

    public void doActionPerformed(ActionEvent event) {
        TreePath[] paths = tree.getSelectedPaths()
        if (paths[0].lastPathComponent == tree.rowHeaderTableTree.model.root) {
            collapseAll(paths)
        } else {
            collapsePaths(paths, true)
        }
    }

    private void collapseAll(TreePath[] paths) {
        ULCTableTree rowView = (ULCTableTree) tree.getRowHeaderView();
        rowView.collapseAll()
        ULCTableTree viewPortView = (ULCTableTree) tree.getViewPortView();
        viewPortView.collapseAll()
        expandPaths(paths, false)
    }

    private void collapsePaths(TreePath[] paths, boolean includingDescendants) {
        tree.getRowHeaderTableTree().collapsePaths(paths, includingDescendants);
        tree.getViewPortTableTree().collapsePaths(paths, includingDescendants);
        expandPaths(paths, false)
    }

    protected void expandPaths(TreePath[] paths, boolean includingDescendants) {
        tree.getRowHeaderTableTree().expandPaths(paths, includingDescendants);
        tree.getViewPortTableTree().expandPaths(paths, includingDescendants);
    }

}

@CompileStatic
class Collapser extends TreeCollapser {

    public Collapser(ULCFixedColumnTableTree tree) {
        super(tree)
    }

    protected void expandPaths(TreePath[] paths, boolean includingDescendants) {

    }

}