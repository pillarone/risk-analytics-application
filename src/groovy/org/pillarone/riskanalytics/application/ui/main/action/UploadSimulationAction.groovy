package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.DetailViewManager
import org.pillarone.riskanalytics.application.ui.main.view.item.UploadBatchUIItem
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.upload.view.UploadBatchView
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class UploadSimulationAction extends SelectionTreeAction {
    UploadBatchUIItem item = new UploadBatchUIItem()

    UploadSimulationAction(ULCTableTree tree) {
        super('UploadSimulationAction', tree)
    }
    @Override
    protected List allowedRoles() {
        return ['ROLE_REVIEWER', 'ROLE_ADMIN']
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if(enabled){
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(item))
            UploadBatchView uploadBatchView = detailViewManager.openDetailView as UploadBatchView
            uploadBatchView.addSimulations(simulations)
        }
    }

    private List<Simulation> getSimulations() {
        List<SimulationNode> simulationNodes = getSelectedObjects(Simulation).findAll {
            it instanceof SimulationNode
        } as List<SimulationNode>
        return simulationNodes ? simulationNodes.itemNodeUIItem.item : []
    }

    private DetailViewManager getDetailViewManager() {
        Holders.grailsApplication.mainContext.getBean('detailViewManager', DetailViewManager)
    }
}