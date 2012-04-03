package org.pillarone.riskanalytics.application.ui.parameterization.model;

import com.ulcjava.base.application.ULCPopupMenu;
import com.ulcjava.base.application.ULCTableTree;
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem;
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.DataEntryPopupMenu;
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.InProductionPopupMenu;
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.InReviewPopupMenu;
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.RejectedPopupMenu;
import org.pillarone.riskanalytics.core.workflow.Status;

import java.util.HashMap;
import java.util.Map;

public class WorkflowParameterizationNode extends ParameterizationNode {

    public WorkflowParameterizationNode(ParameterizationUIItem parameterizationUIItem) {
        super(parameterizationUIItem);
    }

    private Map<String, ULCPopupMenu> statusToMenuMap;
    @Override
    public ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        if (statusToMenuMap == null) {
            statusToMenuMap = new HashMap<String, ULCPopupMenu>();
            statusToMenuMap.put(Status.DATA_ENTRY.getDisplayName(), new DataEntryPopupMenu(tree, this));
            statusToMenuMap.put(Status.REJECTED.getDisplayName(), new RejectedPopupMenu(tree, this));
            statusToMenuMap.put(Status.IN_REVIEW.getDisplayName(), new InReviewPopupMenu(tree, this));
            statusToMenuMap.put(Status.IN_PRODUCTION.getDisplayName(), new InProductionPopupMenu(tree, this));
        }
        return statusToMenuMap.get(getStatus().getDisplayName());
    }
}
