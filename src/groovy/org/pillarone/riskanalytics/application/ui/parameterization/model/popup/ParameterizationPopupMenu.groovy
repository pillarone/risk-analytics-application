package org.pillarone.riskanalytics.application.ui.parameterization.model.popup

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCTableTree
import grails.util.Holders
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.action.ChooseDealAction
import org.pillarone.riskanalytics.application.ui.main.action.workflow.StartWorkflowAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.impl.AbstractParameterNodePopupMenu

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 */
class ParameterizationPopupMenu extends AbstractParameterNodePopupMenu {
    ParameterizationPopupMenu(final ULCTableTree tree, ParameterizationNode node) {
        super(tree, node)
    }

    protected boolean hasRenameAction() { return true; }

    protected boolean hasCreateNewMajorVersionAction() { return true }

    protected boolean addMenuItemsForWorkflowState(ULCTableTree tree, ParameterizationNode node) {
        if (UserContext.hasCurrentUser()) {
            Boolean transactionsEnabled = (Boolean) Holders.grailsApplication.config.getProperty("transactionsEnabled");
            if (transactionsEnabled) {
                add(new ULCMenuItem(new ChooseDealAction(tree, riskAnalyticsMainModel)))
                add(new ULCMenuItem(new StartWorkflowAction(tree, riskAnalyticsMainModel)))
                return true
            }
        }
        return false
    }

    protected boolean hasDeleteAction() { return true }
}
