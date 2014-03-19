package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationProfileActionsPaneModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.action.ApplySimulationProfileAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.action.DeleteSimulationProfileAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.action.SaveSimulationProfileAction
import org.pillarone.riskanalytics.application.ui.util.UIUtils

import static org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationProfileActionsPaneModel.ProfileItem

class SimulationProfileActionsPane {

    static final String PROFILES_COMBO_BOX = 'profilesComboBox'
    static final String APPLY_BUTTON = 'applyButton'
    static final String DELETE_BUTTON = 'deleteButton'
    static final String SAVE_BUTTON = 'saveButton'
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
        profiles = new ULCComboBox(model.simulationProfiles)
        profiles.name = PROFILES_COMBO_BOX
        applyButton = new ULCButton(new ApplySimulationProfileAction(this))
        applyButton.name = APPLY_BUTTON
        deleteButton = new ULCButton(new DeleteSimulationProfileAction(this))
        deleteButton.name = DELETE_BUTTON
        saveButton = new ULCButton(new SaveSimulationProfileAction(this))
        saveButton.name = SAVE_BUTTON
    }

    protected void layout() {
        content = UIUtils.boxLayout("Simulation Profiles") { ULCBoxPane boxPane ->
            boxPane.add(ULCBoxPane.BOX_EXPAND_TOP, profiles)
            boxPane.add(ULCBoxPane.BOX_EXPAND_TOP, applyButton)
            boxPane.add(ULCBoxPane.BOX_EXPAND_TOP, saveButton)
            boxPane.add(ULCBoxPane.BOX_EXPAND_TOP, deleteButton)
        }
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


