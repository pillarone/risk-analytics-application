package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.tree.TreePath
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe

@CompileStatic
class TreeNodeCollapser implements IActionListener {

    ULCTableTree tree

    public void actionPerformed(ActionEvent event) {
        ExceptionSafe.protect {

            TreePath path = tree.getSelectedPath();
            if (path == null) return;
            if (tree.isExpanded(path)) {
                tree.collapsePath(path);
            } else {
                tree.getSelectionModel().setSelectionPath(path.getParentPath());
            }

        }
    }
}