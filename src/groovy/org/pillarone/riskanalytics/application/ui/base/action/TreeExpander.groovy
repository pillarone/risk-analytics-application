package org.pillarone.riskanalytics.application.ui.base.action

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction

class TreeExpander extends ResourceBasedAction {

    ULCFixedColumnTableTree tree

    public TreeExpander(ULCFixedColumnTableTree tree) {
        super("ExpandNode")
        this.tree = tree
    }

    public void doActionPerformed(ActionEvent event) {
        TreePath[] paths = tree.getSelectedPaths()
        if (paths[0].lastPathComponent == tree.rowHeaderTableTree.model.root) {
            tree.expandAll()
        } else {
            tree.expandPaths(paths, true)
        }
    }

}
