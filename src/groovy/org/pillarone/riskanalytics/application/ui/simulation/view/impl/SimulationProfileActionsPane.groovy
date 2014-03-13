package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationProfileActionsPaneModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.action.ApplySimulationProfileAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.action.DeleteSimulationProfileAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.action.SaveSimulationProfileAction

import static org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationProfileActionsPaneModel.ProfileItem

class SimulationProfileActionsPane {

    final SimulationProfileActionsPaneModel model
    private ULCBoxPane content
    private ULCButton deleteButton

    SimulationProfileActionsPane(SimulationProfileActionsPaneModel model) {
        this.model = model
        layout()
    }

    def layout() {
        content = new ULCBoxPane(false)
        ULCComboBox profiles = new ULCComboBox(model.simulationProfiles)
        ULCButton applyButton = new ULCButton(new ApplySimulationProfileAction(this))
        applyButton.enabler = profiles
        ULCButton saveButton = new ULCButton(new SaveSimulationProfileAction(this))
        deleteButton = new ULCButton(new DeleteSimulationProfileAction(this))
        profiles.addActionListener({ ActionEvent event ->
            updateDeleteButtonEnablingState()
        } as IActionListener)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, profiles)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, applyButton)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, saveButton)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, deleteButton)
        updateDeleteButtonEnablingState()
    }

    private void updateDeleteButtonEnablingState() {
        deleteButton.enabled = model.isAllowed(model.simulationProfiles.selectedProfile)
    }

    ULCComponent getContent() {
        content
    }
}


