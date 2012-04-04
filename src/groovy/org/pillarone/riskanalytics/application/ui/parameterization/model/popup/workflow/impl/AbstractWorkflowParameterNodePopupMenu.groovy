package org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.impl

import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.ParameterizationPopupMenu
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 */
class AbstractWorkflowParameterNodePopupMenu extends ParameterizationPopupMenu {
    AbstractWorkflowParameterNodePopupMenu(final ULCTableTree tree, ParameterizationNode node) {
        super(tree, node)
    }

    protected boolean hasRenameAction() { return false; }
    protected boolean hasCreateNewMajorVersionAction() { return false; }
}
