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
        }
        ULCPopupMenu ulcPopupMenu = statusToMenuMap.get(getStatus().getDisplayName());
        if (ulcPopupMenu == null) {
            ulcPopupMenu = createPopupForStatus(getStatus(), tree);
            statusToMenuMap.put(getStatus().getDisplayName(), ulcPopupMenu);
        }
        return ulcPopupMenu;
    }

    private ULCPopupMenu createPopupForStatus(Status status, ULCTableTree tree) {
        switch (status) {
            case NONE:
                return super.getPopupMenu(tree);
            case DATA_ENTRY:
                return new DataEntryPopupMenu(tree, this);
            case IN_REVIEW:
                return new InReviewPopupMenu(tree, this);
            case REJECTED:
                return new RejectedPopupMenu(tree, this);
            case IN_PRODUCTION:
                return new InProductionPopupMenu(tree, this);
            default:
                return null;
        }
    }
}
