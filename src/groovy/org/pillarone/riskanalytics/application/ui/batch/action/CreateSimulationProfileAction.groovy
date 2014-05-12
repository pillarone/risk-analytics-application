package org.pillarone.riskanalytics.application.ui.batch.action
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.batch.model.BatchRowInfo
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.SimulationSettingsChangedEvent
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationSettingsUIItem
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
        riskAnalyticsEventBus.post(new SimulationSettingsChangedEvent(null, parameterization, simulation.modelClass))
    }


    boolean isEnabled() {
        batchView.selectedBatchRowInfos.size() == 1
    }
}
