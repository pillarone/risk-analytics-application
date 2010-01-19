package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction

class RemoveDynamicSubComponent extends ResourceBasedAction {

    def tree

    public RemoveDynamicSubComponent(tree) {
        super("RemoveDynamicSubComponent")
        this.tree = tree
    }

    public void doActionPerformed(ActionEvent event) {
        def node = tree.selectedPath.lastPathComponent
        tree.model.removeComponentNode(node)
    }

}
