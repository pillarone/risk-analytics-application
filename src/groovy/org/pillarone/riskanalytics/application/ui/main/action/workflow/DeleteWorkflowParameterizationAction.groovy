package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.action.DeleteAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.workflow.StatusChangeService

class DeleteWorkflowParameterizationAction extends DeleteAction {

    DeleteWorkflowParameterizationAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super(tree, model)
    }

    @Override
    protected void removeItem(List<AbstractUIItem> selectedItems) {
        selectedItems.each { selectedItem ->
            if (selectedItem instanceof ParameterizationUIItem) {
                StatusChangeService.service.clearAudit(selectedItem.item as Parameterization)
            }
            super.removeItem(selectedItem)
        }
    }
}
