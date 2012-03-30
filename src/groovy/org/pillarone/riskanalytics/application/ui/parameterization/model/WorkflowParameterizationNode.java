package org.pillarone.riskanalytics.application.ui.parameterization.model;

import com.ulcjava.base.application.ULCMenuItem;
import com.ulcjava.base.application.ULCPopupMenu;
import com.ulcjava.base.application.ULCTableTree;
import org.pillarone.riskanalytics.application.ui.main.action.workflow.CreateNewWorkflowVersionAction;
import org.pillarone.riskanalytics.application.ui.main.action.workflow.RejectWorkflowAction;
import org.pillarone.riskanalytics.application.ui.main.action.workflow.SendToProductionAction;
import org.pillarone.riskanalytics.application.ui.main.action.workflow.SendToReviewAction;
import org.pillarone.riskanalytics.application.ui.main.view.SendToProductionMenuItem;
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem;
import org.pillarone.riskanalytics.core.workflow.Status;

import java.util.HashMap;
import java.util.Map;

public class WorkflowParameterizationNode extends ParameterizationNode {

    public WorkflowParameterizationNode(ParameterizationUIItem parameterizationUIItem) {
        super(parameterizationUIItem);
    }

    private static Map<String, ULCPopupMenu> statusToMenuMap;
    @Override
    public ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        if (statusToMenuMap == null) {
            statusToMenuMap = new HashMap<String, ULCPopupMenu>();
            statusToMenuMap.put(Status.DATA_ENTRY.getDisplayName(), getDataEntryPopupMenu(tree));
            statusToMenuMap.put(Status.REJECTED.getDisplayName(), getRejectedPopupMenu(tree));
            statusToMenuMap.put(Status.IN_REVIEW.getDisplayName(), getInReviewPopupMenu(tree));
            statusToMenuMap.put(Status.IN_PRODUCTION.getDisplayName(), getInProductionPopupMenu(tree));
        }
        return statusToMenuMap.get(getStatus().getDisplayName());
    }

    private ULCPopupMenu getDataEntryPopupMenu(ULCTableTree tree) {
        return new AbstractWorkflowParameterNodePopupMenu(tree) {
            @Override
            protected boolean addMenuItemsForWorkflowState(ULCTableTree tree) {
                add(new ULCMenuItem(new SendToReviewAction(tree, getAbstractUIItem().mainModel)));
                return true;
            }

            protected boolean hasDeleteAction() { return true; }
        };
    }

    private ULCPopupMenu getRejectedPopupMenu(ULCTableTree tree) {
        return new AbstractWorkflowParameterNodePopupMenu(tree) {
            protected boolean addMenuItemsForWorkflowState(ULCTableTree tree) {
                // none to add
                return false;
            }
            protected boolean hasDeleteAction() { return false; }
        };
    }

    private ULCPopupMenu getInReviewPopupMenu(ULCTableTree tree) {
        return new AbstractWorkflowParameterNodePopupMenu(tree) {
            protected boolean addMenuItemsForWorkflowState(ULCTableTree tree) {
                SendToProductionMenuItem sendToProductionMenuItem = new SendToProductionMenuItem(new SendToProductionAction(tree, getAbstractUIItem().mainModel));
                add(sendToProductionMenuItem);
                tree.addTreeSelectionListener(sendToProductionMenuItem);
                add(new ULCMenuItem(new RejectWorkflowAction(tree, getAbstractUIItem().mainModel)));
                return true;
            }

            @Override
            protected boolean hasDeleteAction() { return false; }
        };
    }

    private ULCPopupMenu getInProductionPopupMenu(ULCTableTree tree) {
        return new AbstractWorkflowParameterNodePopupMenu(tree) {
            protected boolean addMenuItemsForWorkflowState(ULCTableTree tree) {
                add(new ULCMenuItem(new CreateNewWorkflowVersionAction(tree, getAbstractUIItem().mainModel)));
                return true;
            }

            @Override
            protected boolean hasDeleteAction() { return false; }
        };
    }

    private abstract class AbstractWorkflowParameterNodePopupMenu extends ParameterizationNode.AbstractParameterNodePopupMenu {
        AbstractWorkflowParameterNodePopupMenu(ULCTableTree tree) {
            super(tree);
        }

        protected boolean hasRenameAction() { return false; }
        protected boolean hasCreateNewMajorVersionAction() { return false; }
    }
}
