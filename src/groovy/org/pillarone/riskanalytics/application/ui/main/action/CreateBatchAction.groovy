package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.NodeNameDialog
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class CreateBatchAction extends SelectionTreeAction {

    CreateBatchAction(ULCTableTree tree) {
        super("CreateBatch", tree)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        List<ParameterizationNode> parameterizationNodes = getSelectedObjects(Parameterization).findAll {
            it instanceof ParameterizationNode
        } as List<ParameterizationNode>
        Batch batch = batchRunService.createBatch(parameterizationNodes.itemNodeUIItem.item)
        BatchUIItem batchUIItem = new BatchUIItem(batch)
        NodeNameDialog nameDialog = new NodeNameDialog(UlcUtilities.getWindowAncestor(tree), batchUIItem)
        nameDialog.okAction = { String name ->
            batch.name = name
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(batchUIItem))
            batch.changed = true
        }
        nameDialog.show()
    }

    private BatchRunService getBatchRunService() {
        Holders.grailsApplication.mainContext.getBean('batchRunService', BatchRunService)
    }
}
