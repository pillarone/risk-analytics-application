package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.main.action.workflow.AbstractWorkflowAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SendToProductionMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    AbstractWorkflowAction workflowAction

    public SendToProductionMenuItem(AbstractWorkflowAction workflowAction) {
        super(workflowAction)
        this.workflowAction = workflowAction;
    }

    void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        setEnabled(workflowAction.isEnabled())
    }


}
