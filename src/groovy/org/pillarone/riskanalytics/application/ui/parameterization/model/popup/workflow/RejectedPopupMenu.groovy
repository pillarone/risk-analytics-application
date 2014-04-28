package org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.ParameterizationPopupMenu

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 */
class RejectedPopupMenu extends ParameterizationPopupMenu {
    RejectedPopupMenu(final ULCTableTree tree, ParameterizationNode node) {
        super(tree, node)
    }

    @Override
    protected boolean hasDeleteAction() { return true } //Otherwise Worflow subtrees become cul-de-sacs

    @Override
    protected boolean addMenuItemsForWorkflowState(ULCTableTree tree, ParameterizationNode node) {
        // none to add
        return false;
    }
}
