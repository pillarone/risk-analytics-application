package org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.view.EnabledCheckingMenuItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.ParameterizationPopupMenu
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.main.action.workflow.SendToProductionAction
import org.pillarone.riskanalytics.application.ui.main.action.workflow.RejectWorkflowAction

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 */
class InReviewPopupMenu extends ParameterizationPopupMenu {
    InReviewPopupMenu(final ULCTableTree tree, ParameterizationNode node) {
        super(tree, node)
    }

    @Override
    protected boolean addMenuItemsForWorkflowState(ULCTableTree tree, ParameterizationNode node) {
        add(new EnabledCheckingMenuItem(new SendToProductionAction(tree)));
        add(new EnabledCheckingMenuItem(new RejectWorkflowAction(tree)));
        return true;
    }

}
