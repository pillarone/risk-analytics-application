package org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.impl.AbstractWorkflowParameterNodePopupMenu
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.main.view.SendToProductionMenuItem
import org.pillarone.riskanalytics.application.ui.main.action.workflow.SendToProductionAction
import com.ulcjava.base.application.ULCMenuItem
import org.pillarone.riskanalytics.application.ui.main.action.workflow.RejectWorkflowAction

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 */
class InReviewPopupMenu extends AbstractWorkflowParameterNodePopupMenu {
    InReviewPopupMenu(final ULCTableTree tree, ParameterizationNode node) {
        super(tree, node)
    }

    @Override
    protected boolean addMenuItemsForWorkflowState(ULCTableTree tree, ParameterizationNode node) {
        SendToProductionMenuItem sendToProductionMenuItem = new SendToProductionMenuItem(new SendToProductionAction(tree, node.getItemNodeUIItem().mainModel));
        add(sendToProductionMenuItem);
        tree.addTreeSelectionListener(sendToProductionMenuItem);
        add(new ULCMenuItem(new RejectWorkflowAction(tree, node.getItemNodeUIItem().mainModel)));
        return true;
    }

    @Override
    protected boolean hasDeleteAction() { return false; }
}
