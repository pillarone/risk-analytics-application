package org.pillarone.riskanalytics.application.ui.parameterization.model;

import com.ulcjava.base.application.ULCPopupMenu;
import com.ulcjava.base.application.ULCTableTree;
import com.ulcjava.base.application.util.Font;
import org.pillarone.riskanalytics.application.ui.base.model.VersionedItemNode;
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem;
import org.pillarone.riskanalytics.application.ui.parameterization.model.popup.ParameterizationPopupMenu;
import org.pillarone.riskanalytics.core.simulation.item.Parameterization;
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem;

public class ParameterizationNode extends VersionedItemNode implements IReportableNode {


    public ParameterizationNode(ParameterizationUIItem parameterizationUIItem) {
        super(parameterizationUIItem, false);
    }

    @Override
    public ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        return new ParameterizationPopupMenu(tree, this);
    }

    public boolean isValid() {
        return getParameterization().isValid();
    }

    public Status getStatus() {
        return getParameterization().getStatus();
    }

    private Parameterization getParameterization() {
        return ((Parameterization)getAbstractUIItem().getItem());
    }

    @Override
    public Font getFont(String fontName, int fontSize) {
        return new Font(fontName, !isValid() ? Font.ITALIC : Font.PLAIN, fontSize);
    }

    @Override
    public String getToolTip() {
        return getParameterization().getStatus() == Status.NONE ? String.valueOf("") : getParameterization().getStatus().getDisplayName();
    }

    List<Class> modelsToReportOn() {
        return [ abstractUIItem.model.getClass() ]
    }

    List<ModellingItem> modellingItemsForReport() {
        return [((ModellingItem) ( (ModellingUIItem) abstractUIItem).item)]
    }
}
