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
        SimulationProfile item = loadSelectedItem()
        if (!(item && item.id)) {
            new I18NAlert(UlcUtilities.getRootPane(actionsPane.content), 'ProfileNotExistent').show()
            return
        }
        actionsPane.model.delete(item)
    }

    private SimulationProfile loadSelectedItem() {
        def selectedName = actionsPane.model.simulationProfiles.selectedItem as String
        if (!selectedName) {
            return null
        }
        SimulationProfile item = new SimulationProfile(selectedName)
        item.load(true)
        item
    }
}
