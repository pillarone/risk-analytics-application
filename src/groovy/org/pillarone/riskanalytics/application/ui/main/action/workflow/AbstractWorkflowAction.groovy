package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.workflow.StatusChangeService
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import com.ulcjava.base.application.ULCTree
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import com.ulcjava.base.application.tree.ITreeNode


abstract class AbstractWorkflowAction extends SelectionTreeAction {

    private StatusChangeService service = StatusChangeService.getService()

    public AbstractWorkflowAction(String name, ULCTree tree, P1RATModel model) {
        super(name, tree, model)
    }

    final void doActionPerformed(ActionEvent event) {
        Parameterization item = getSelectedItem()
        item.load()
        Parameterization parameterization = service.changeStatus(item, toStatus())
        if (!item.is(parameterization)) {
            model.selectionTreeModel.addNodeForItem(parameterization)
        } else {
            ITreeNode paramNode = model.selectionTreeModel.findNodeForItem(model.selectionTreeModel.root, parameterization)
            model.selectionTreeModel.nodeChanged(paramNode)
        }

    }

    abstract Status toStatus()

}
