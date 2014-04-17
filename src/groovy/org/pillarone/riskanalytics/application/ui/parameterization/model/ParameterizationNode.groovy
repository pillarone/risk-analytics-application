package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.util.Font
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.ParameterizationPopupMenu
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.DataEntryPopupMenu
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.InProductionPopupMenu
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.InReviewPopupMenu
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.workflow.RejectedPopupMenu
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.workflow.Status

import static org.pillarone.riskanalytics.core.workflow.Status.*

@CompileStatic
class ParameterizationNode extends ItemNode implements IReportableNode {

    private Map<String, ULCPopupMenu> statusToMenuMap

    ParameterizationNode(ParameterizationUIItem parameterizationUIItem) {
        super(parameterizationUIItem, false)
    }

    @Override
    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        if (statusToMenuMap == null) {
            statusToMenuMap = new HashMap<String, ULCPopupMenu>()
        }
        ULCPopupMenu ulcPopupMenu = statusToMenuMap[status.displayName]
        if (ulcPopupMenu == null) {
            ulcPopupMenu = createPopupForStatus(status, tree)
            statusToMenuMap[status.displayName] = ulcPopupMenu
        }
        return ulcPopupMenu
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

    boolean isValid() {
        itemNodeUIItem.item.valid;
    }

    Status getStatus() {
        itemNodeUIItem.item.status
    }

    @Override
    Font getFont(String fontName, int fontSize) {
        new Font(fontName, valid ? Font.PLAIN : Font.ITALIC, fontSize)
    }

    @Override
    String getToolTip() {
        itemNodeUIItem.item.nameAndVersion
    }

    List<Class> modelsToReportOn() {
        [itemNodeUIItem.model.class]
    }

    List<ModellingItem> modellingItemsForReport() {
        [itemNodeUIItem.item]
    }

    @Override
    ParameterizationUIItem getItemNodeUIItem() {
        return super.itemNodeUIItem as ParameterizationUIItem
    }
}
