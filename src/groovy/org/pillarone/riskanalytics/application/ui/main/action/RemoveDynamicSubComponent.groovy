package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.view.ComponentUtils
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel

class RemoveDynamicSubComponent extends ResourceBasedAction {

    def tree
    ParameterViewModel model

    public RemoveDynamicSubComponent(tree, ParameterViewModel model) {
        super("RemoveDynamicSubComponent")
        this.tree = tree
        this.model = model
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true));
    }

    public void doActionPerformed(ActionEvent event) {
        def node = tree.selectedPath.lastPathComponent
        if (node && ComponentUtils.isDynamicComposedSubComponentNode(node))
            tree.model.removeComponentNode(node)
    }

    public boolean isEnabled() {
        return super.isEnabled() && !model.paramterTableTreeModel.readOnly;
    }


}
