package org.pillarone.riskanalytics.application.ui.simulation.view.impl.action

import com.ulcjava.base.application.ULCRootPane
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationProfileActionsPaneModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationProfileActionsPane

class SaveSimulationProfileAction extends ResourceBasedAction {

    private static final String SAVE_SIMULATION_PROFILE = 'SaveSimulationProfile'

    private final SimulationProfileActionsPane simulationProfileActionsPane
    private SimulationProfileNameDialog profileNameDialog

    SaveSimulationProfileAction(SimulationProfileActionsPane simulationProfileActionsPane) {
        super(SAVE_SIMULATION_PROFILE)
        this.simulationProfileActionsPane = simulationProfileActionsPane
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        dialog.name = profileName
        dialog.show()
    }

    private String getProfileName() {
        model.simulationProfiles.selectedProfileName
    }

    private SimulationProfileNameDialog getDialog() {
        profileNameDialog ?: createProfileNameDialog()
    }

    private SimulationProfileNameDialog createProfileNameDialog() {
        new SimulationProfileNameDialog(rootPane, createOkAction(), model.modelClass, model.currentUser()?.username)
    }

    private Closure<Boolean> createOkAction() {
        { String name -> model.saveCurrentProfile(name) }
    }

    private SimulationProfileActionsPaneModel getModel() {
        simulationProfileActionsPane.model
    }

    private ULCRootPane getRootPane() {
        UlcUtilities.getRootPane(simulationProfileActionsPane.content)
    }
}



