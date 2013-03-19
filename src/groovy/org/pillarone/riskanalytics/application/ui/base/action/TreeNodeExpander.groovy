package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe

class TreeNodeExpander implements IActionListener {

    ULCTableTree tree

    public void actionPerformed(ActionEvent event) {
        ExceptionSafe.protect {

            TreePath path = tree.getSelectedPath();
            if (path == null) return;
            if (tree.isExpanded(path)) {
                tree.setRowSelection(tree.getSelectedRow() + 1);
                tree.scrollCellToVisible(tree.getSelectedPath(), tree.getModel().getTreeColumn());
            } else {
                tree.expandPath(path);
            }

        }
    }
}