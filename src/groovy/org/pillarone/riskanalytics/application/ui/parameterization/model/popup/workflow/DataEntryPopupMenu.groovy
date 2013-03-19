package org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.action.workflow.SendToReviewAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.impl.AbstractWorkflowParameterNodePopupMenu
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 */
class DataEntryPopupMenu extends AbstractWorkflowParameterNodePopupMenu {
    DataEntryPopupMenu(final ULCTableTree tree, ParameterizationNode node) {
        super(tree, node)
    }

    @Override
    protected boolean addMenuItemsForWorkflowState(ULCTableTree tree, ParameterizationNode node) {
        add(new ULCMenuItem(new SendToReviewAction(tree, node.getAbstractUIItem().mainModel)));
        return true;
    }

    @Override
    protected boolean hasDeleteAction() { return true; }
}
