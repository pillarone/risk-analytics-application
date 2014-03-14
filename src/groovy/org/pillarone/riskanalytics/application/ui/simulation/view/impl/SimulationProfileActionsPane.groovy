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
    private ULCComboBox profiles
    private ULCButton applyButton
    private ULCButton saveButton

    SimulationProfileActionsPane(SimulationProfileActionsPaneModel model) {
        this.model = model
        createComponents()
        layout()
        bind()
        updateDeleteButtonEnablingState()
    }

    protected void createComponents() {
        content = new ULCBoxPane(false)
        profiles = new ULCComboBox(model.simulationProfiles)
        applyButton = new ULCButton(new ApplySimulationProfileAction(this))
        deleteButton = new ULCButton(new DeleteSimulationProfileAction(this))
        saveButton = new ULCButton(new SaveSimulationProfileAction(this))
    }

    protected void layout() {
        content.add(ULCBoxPane.BOX_EXPAND_TOP, profiles)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, applyButton)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, saveButton)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, deleteButton)
    }

    protected void bind() {
        applyButton.enabler = profiles
        profiles.addActionListener({ ActionEvent event ->
            updateDeleteButtonEnablingState()
        } as IActionListener)
    }

    protected void updateDeleteButtonEnablingState() {
        deleteButton.enabled = model.currentAllowedToDelete
    }

    ULCComponent getContent() {
        content
    }
}


