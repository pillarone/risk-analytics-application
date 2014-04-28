package org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.impl

import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.ParameterizationPopupMenu
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 */

// Seems to be a useless class that adds no value and just adds extra layer of inheritance.
// TODO delete this class after moving its subclasses up one level in hierarchy.
//
@Deprecated
abstract class AbstractWorkflowParameterNodePopupMenu extends ParameterizationPopupMenu {
    AbstractWorkflowParameterNodePopupMenu(final ULCTableTree tree, ParameterizationNode node) {
        super(tree, node)
    }

    protected boolean hasRenameAction() { return false; }
    protected boolean hasCreateNewMajorVersionAction() { return false; }
}
