package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.base.model.VersionedItemNode
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.ParameterizationPopupMenu
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.DataEntryPopupMenu
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.InProductionPopupMenu
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.InReviewPopupMenu
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.RejectedPopupMenu
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.workflow.Status

import static org.pillarone.riskanalytics.core.workflow.Status.*

public class ParameterizationNode extends VersionedItemNode implements IReportableNode {

    private Map<String, ULCPopupMenu> statusToMenuMap;

    public ParameterizationNode(ParameterizationUIItem parameterizationUIItem) {
        super(parameterizationUIItem, false);
    }

    @Override
    public ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        if (statusToMenuMap == null) {
            statusToMenuMap = new HashMap<String, ULCPopupMenu>()
        }
        ULCPopupMenu ulcPopupMenu = statusToMenuMap.get(status.displayName)
        if (ulcPopupMenu == null) {
            ulcPopupMenu = createPopupForStatus(status, tree)
            statusToMenuMap.put(status.displayName, ulcPopupMenu)
        }
        return ulcPopupMenu;
    }

    private ULCPopupMenu createPopupForStatus(Status status, ULCTableTree tree) {
        switch (status) {
            case NONE:
                return new ParameterizationPopupMenu(tree, this)
            case DATA_ENTRY:
                return new DataEntryPopupMenu(tree, this)
            case IN_REVIEW:
                return new InReviewPopupMenu(tree, this)
            case REJECTED:
                return new RejectedPopupMenu(tree, this)
            case IN_PRODUCTION:
                return new InProductionPopupMenu(tree, this)
            default:
                return null
        }
    }

    public boolean isValid() {
        return parameterization.valid;
    }

    public Status getStatus() {
        return parameterization.status
    }

    public Parameterization getParameterization() {
        return ((Parameterization) itemNodeUIItem.item)
    }

    @Override
    public Font getFont(String fontName, int fontSize) {
        return new Font(fontName, !isValid() ? Font.ITALIC : Font.PLAIN, fontSize)
    }

    @Override
    public String getToolTip() {
        return getParameterization().getStatus() == NONE ? String.valueOf("") : getParameterization().getStatus().getDisplayName();
    }

    List<Class> modelsToReportOn() {
        return [itemNodeUIItem.model.getClass()]
    }

    List<ModellingItem> modellingItemsForReport() {
        return [((ModellingItem) ((ModellingUIItem) itemNodeUIItem).item)]
    }
}
