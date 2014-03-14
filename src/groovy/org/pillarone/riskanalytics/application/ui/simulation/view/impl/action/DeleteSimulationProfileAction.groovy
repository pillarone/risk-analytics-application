package org.pillarone.riskanalytics.application.ui.simulation.view.impl.action

import com.ulcjava.base.application.ULCRootPane
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationProfileActionsPane
import org.pillarone.riskanalytics.application.ui.util.I18NAlert

class DeleteSimulationProfileAction extends ResourceBasedAction {

    private static final String DELETE_SIMULATION_PROFILE = 'DeleteSimulationProfile'
    private static final String PROFILE_NOT_EXISTENT = 'ProfileNotExistent'
    private final SimulationProfileActionsPane actionsPane

    DeleteSimulationProfileAction(SimulationProfileActionsPane actionsPane) {
        super(DELETE_SIMULATION_PROFILE)
        this.actionsPane = actionsPane
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (!actionsPane.model.deleteCurrentProfile()) {
            showAlert()
        }
    }

    private void showAlert() {
        new I18NAlert(rootPane, PROFILE_NOT_EXISTENT).show()
    }

    private ULCRootPane getRootPane() {
        UlcUtilities.getRootPane(actionsPane.content)
    }
}
