package org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow

import com.ulcjava.base.application.ULCTableTree
import grails.util.Holders
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.action.ChooseDealAction
import org.pillarone.riskanalytics.application.ui.main.action.workflow.StartWorkflowAction
import org.pillarone.riskanalytics.application.ui.main.view.EnabledCheckingMenuItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.ParameterizationPopupMenu

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 */
class StatusNonePopupMenu extends ParameterizationPopupMenu {
    static private long countInstances = 0;
    StatusNonePopupMenu(final ULCTableTree tree, ParameterizationNode node) {
        super(tree, node)
        ++countInstances; //Want to see whether millions of these are made
    }

    @Override
    protected boolean hasCreateNewMajorVersionAction() { return true; }
    @Override
    protected boolean hasDeleteAction() { return true; }
    @Override
    protected boolean hasRenameAction() { return true; }

    @Override
    protected boolean addMenuItemsForWorkflowState(ULCTableTree tree, ParameterizationNode node) {
        if (UserContext.hasCurrentUser()) {
            Boolean transactionsEnabled = (Boolean) Holders.grailsApplication.config.getProperty("transactionsEnabled");
            if (transactionsEnabled) {
                add(new EnabledCheckingMenuItem(new ChooseDealAction(tree)))
                add(new EnabledCheckingMenuItem(new StartWorkflowAction(tree)))
                return true
            }
        }
        return false
    }

}
