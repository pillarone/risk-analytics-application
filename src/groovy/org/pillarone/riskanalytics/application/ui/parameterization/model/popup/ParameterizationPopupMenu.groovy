package org.pillarone.riskanalytics.application.ui.parameterization.model.popup

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.impl.AbstractParameterNodePopupMenu

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 */
abstract class ParameterizationPopupMenu extends AbstractParameterNodePopupMenu {
    ParameterizationPopupMenu(final ULCTableTree tree, ParameterizationNode node) {
        super(tree, node)
    }


    @Override
    protected boolean hasCreateNewMajorVersionAction() { return false }
    @Override
    protected boolean hasDeleteAction() { return false }
    @Override
    protected boolean hasRenameAction() { return false }

//    addMenuItemsForWorkflowState() moved to subclass StatusNonePopupMenu

}
