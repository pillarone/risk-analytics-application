package org.pillarone.riskanalytics.application.ui.simulation.view.impl.action

import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationProfileActionsPane
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

class DeleteSimulationProfileAction extends ResourceBasedAction {

    public static final String DELETE_SIMULATION_PROFILE = 'DeleteSimulationProfile'
    private final SimulationProfileActionsPane actionsPane

    DeleteSimulationProfileAction(SimulationProfileActionsPane actionsPane) {
        super(DELETE_SIMULATION_PROFILE)
        this.actionsPane = actionsPane
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        SimulationProfile item = actionsPane.model.loadSelectedProfile()
        if (!actionsPane.model.delete(item)) {
            new I18NAlert(UlcUtilities.getRootPane(actionsPane.content), 'ProfileNotExistent').show()
        }
    }
}
