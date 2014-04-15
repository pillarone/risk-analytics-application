package org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.impl.AbstractWorkflowParameterNodePopupMenu
import com.ulcjava.base.application.ULCMenuItem
import org.pillarone.riskanalytics.application.ui.main.action.workflow.CreateNewWorkflowVersionAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 */
class InProductionPopupMenu extends AbstractWorkflowParameterNodePopupMenu {
    InProductionPopupMenu(final ULCTableTree tree, ParameterizationNode node) {
        super(tree, node)
    }

    @Override
    protected boolean addMenuItemsForWorkflowState(ULCTableTree tree, ParameterizationNode node) {
        add(new ULCMenuItem(new CreateNewWorkflowVersionAction(tree, node.getItemNodeUIItem().mainModel)));
        return true;
    }

    @Override
    protected boolean hasDeleteAction() { return false; }
}
