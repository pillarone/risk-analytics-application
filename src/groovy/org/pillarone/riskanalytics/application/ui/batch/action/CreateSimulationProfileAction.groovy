package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.batch.model.BatchRowInfo
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.DetailViewManager
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationSettingsUIItem
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationConfigurationView
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class CreateSimulationProfileAction extends ResourceBasedAction {

    private final BatchView batchView

    CreateSimulationProfileAction(BatchView batchView) {
        super("CreateSimulationProfile")
        this.batchView = batchView
    }

    void doActionPerformed(ActionEvent event) {
        BatchRowInfo info = batchView.selectedBatchRowInfos.first()
        Simulation simulation = new Simulation('Simulation')
        Parameterization parameterization = info.parameterization
        simulation.modelClass = info.modelClass
        riskAnalyticsEventBus.post(new OpenDetailViewEvent(new SimulationSettingsUIItem(simulation)))
        SimulationConfigurationView view = detailViewManager.openDetailView as SimulationConfigurationView
        view.model.parameterization = parameterization
    }


    DetailViewManager getDetailViewManager() {
        Holders.grailsApplication.mainContext.getBean('detailViewManager', DetailViewManager)
    }

    boolean isEnabled() {
        batchView.selectedBatchRowInfos.size() == 1
    }
}
