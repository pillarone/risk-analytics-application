package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.view.DetailViewManager
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class AddToOpenBatchAction extends SelectionTreeAction {

    AddToOpenBatchAction(ULCTableTree tree) {
        super("AddToOpenBatch", tree)
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
            BatchView batchView = detailViewManager.openDetailView as BatchView
            batchView.addParameterizations(parameterizationNodes.itemNodeUIItem.item)
        }
    }

    private DetailViewManager getDetailViewManager() {
        Holders.grailsApplication.mainContext.getBean('detailViewManager', DetailViewManager)
    }

    @Override
    boolean isEnabled() {
        detailViewManager.openDetailView instanceof BatchView
    }
}
