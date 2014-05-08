package org.pillarone.riskanalytics.application.ui.batch.view.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.batch.model.BatchRowInfo
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.core.simulation.item.Simulation

import static org.pillarone.riskanalytics.core.simulation.SimulationState.FINISHED

class OpenResultsAction extends ResourceBasedAction {

    private final BatchView batchView

    OpenResultsAction(BatchView batchView) {
        super('OpenResults')
        this.batchView = batchView
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (enabled) {
            Simulation simulation = batchView.selectedBatchRowInfos.first().simulation
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(UIItemFactory.createItem(simulation)))
        }
    }

    @Override
    boolean isEnabled() {
        List<BatchRowInfo> infos = batchView.selectedBatchRowInfos
        return infos.size() == 1 && infos.first().simulation?.simulationState == FINISHED
    }
}
