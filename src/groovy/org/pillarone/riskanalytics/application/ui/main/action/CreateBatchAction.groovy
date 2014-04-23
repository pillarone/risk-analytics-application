package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class CreateBatchAction extends SelectionTreeAction {

    CreateBatchAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("CreateBatch", tree, model)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        List<ParameterizationNode> parameterizationNodes = getSelectedObjects(Parameterization).findAll {
            it instanceof ParameterizationNode
        } as List<ParameterizationNode>
        Batch batch = batchRunService.createBatch(parameterizationNodes.itemNodeUIItem.item)
        model.openItem(null, new BatchUIItem(batch))
    }

    private BatchRunService getBatchRunService() {
        Holders.grailsApplication.mainContext.getBean('batchRunService', BatchRunService)
    }
}
