package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.ULCTableTree


class GoToParentNodeAction extends ExceptionSafeAction {

    private ULCTableTree tree

    GoToParentNodeAction(ULCTableTree tree) {
        this.tree = tree
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (tree.selectedPath != null) {
            tree.getSelectionModel().setSelectionPath(tree.selectedPath.parentPath)
        }
    }
}
