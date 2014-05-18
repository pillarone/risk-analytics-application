package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.view.DetailViewManager
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class AddToOpenBatchAction extends SelectionTreeAction {

    AddToOpenBatchAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("AddToOpenBatch", tree, model)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (!enabled) {
            return
        }
        List<ParameterizationNode> parameterizationNodes = getSelectedObjects(Parameterization).findAll {
            it instanceof ParameterizationNode
        } as List<ParameterizationNode>
        if (parameterizationNodes) {
            BatchView batchView = detailViewManager.getDetailViewForItem(model.currentItem as BatchUIItem) as BatchView
            batchView.addParameterizations(parameterizationNodes.itemNodeUIItem.item)
        }
    }

    private DetailViewManager getDetailViewManager() {
        Holders.grailsApplication.mainContext.getBean('detailViewManager', DetailViewManager)
    }

    @Override
    boolean isEnabled() {
        AbstractUIItem item = model.currentItem
        if (item instanceof BatchUIItem) {
            return !item.item.executed && detailViewManager.getDetailViewForItem(item)
        }
        false
    }
}
